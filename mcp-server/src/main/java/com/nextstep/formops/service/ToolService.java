package com.nextstep.formops.service;

import com.nextstep.formops.model.CheckMissingFieldsRequest;
import com.nextstep.formops.model.CheckMissingFieldsResponse;
import com.nextstep.formops.model.CreateFormDraftRequest;
import com.nextstep.formops.model.CreateFormDraftResponse;
import com.nextstep.formops.model.CreateTaskRequest;
import com.nextstep.formops.model.CreateTaskResponse;
import com.nextstep.formops.model.FieldDefinition;
import com.nextstep.formops.model.FormRegistryEntry;
import com.nextstep.formops.model.FormSchema;
import com.nextstep.formops.model.MissingField;
import com.nextstep.formops.model.PlanFormsRequest;
import com.nextstep.formops.model.PlanFormsResponse;
import com.nextstep.formops.model.PlannedForm;
import com.nextstep.formops.model.ProcessStep;
import com.nextstep.formops.model.ProcessTemplate;
import com.nextstep.formops.model.TaskInstance;
import com.nextstep.formops.model.TaskStatusResponse;
import com.nextstep.formops.model.TaskStepStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ToolService {
    private final ProcessKnowledgeService knowledgeService;
    private final TaskStateService taskStateService;
    private final ConditionEvaluator conditionEvaluator;

    public ToolService(ProcessKnowledgeService knowledgeService, TaskStateService taskStateService, ConditionEvaluator conditionEvaluator) {
        this.knowledgeService = knowledgeService;
        this.taskStateService = taskStateService;
        this.conditionEvaluator = conditionEvaluator;
    }

    public CreateTaskResponse createTask(CreateTaskRequest request) {
        TaskInstance task = taskStateService.createTask(request);
        return new CreateTaskResponse(task.getTaskId(), task.getDashboardUrl());
    }

    public PlanFormsResponse planForms(PlanFormsRequest request) {
        TaskInstance task = taskStateService.getTask(request.taskId());
        ProcessTemplate process = knowledgeService.processByCode(task.getProcessCode());
        List<PlannedForm> plannedForms = process.steps().stream()
                .filter(step -> conditionEvaluator.allMatch(step.conditions(), task.getContext()))
                .sorted(Comparator.comparing(ProcessStep::order))
                .map(step -> toPlannedForm(step, task))
                .toList();

        plannedForms.forEach(form -> taskStateService.upsertStep(task, new TaskStepStatus(
                form.order(),
                form.formCode(),
                form.formName(),
                form.status(),
                null,
                null,
                Map.of(),
                null
        )));

        task.setStatus("PLANNED");
        task.setCurrentStep(firstReadyForm(plannedForms));
        task.setNextAction(process.defaultNextAction());
        return new PlanFormsResponse(process.processName(), process.processCode(), plannedForms);
    }

    public CheckMissingFieldsResponse checkMissingFields(CheckMissingFieldsRequest request) {
        TaskInstance task = taskStateService.getTask(request.taskId());
        Map<String, Object> mergedInput = mergedInput(task, request.input());
        FormSchema schema = knowledgeService.formSchema(request.formCode());
        List<MissingField> missingFields = missingFields(schema, mergedInput);
        return new CheckMissingFieldsResponse(request.formCode(), schema.formName(), missingFields);
    }

    public CreateFormDraftResponse createFormDraft(CreateFormDraftRequest request) {
        TaskInstance task = taskStateService.getTask(request.taskId());
        taskStateService.mergeContext(task, request.input());

        CheckMissingFieldsResponse missing = checkMissingFields(new CheckMissingFieldsRequest(
                request.taskId(),
                request.formCode(),
                request.input()
        ));

        if (!missing.missingFields().isEmpty()) {
            taskStateService.upsertStep(task, new TaskStepStatus(
                    orderOf(task, request.formCode()),
                    request.formCode(),
                    missing.formName(),
                    "WAITING_FOR_INPUT",
                    null,
                    null,
                    Map.of(),
                    "Missing required fields: " + missing.missingFields().stream().map(MissingField::name).toList()
            ));
            task.setStatus("WAITING_FOR_INPUT");
            task.setCurrentStep("等待補齊「" + missing.formName() + "」欄位");
            task.setNextAction("補齊缺漏欄位後再次呼叫 create_form_draft");
            return new CreateFormDraftResponse(
                    false,
                    request.formCode(),
                    missing.formName(),
                    "WAITING_FOR_INPUT",
                    null,
                    task.getDashboardUrl(),
                    null,
                    Map.of(),
                    "Missing required fields: " + missing.missingFields().stream().map(MissingField::name).toList()
            );
        }

        FormSchema schema = knowledgeService.formSchema(request.formCode());
        Map<String, Object> outputData = taskStateService.mockOutputFor(request.formCode());
        task.getContext().putAll(outputData);
        String draftNo = draftNo(request.formCode());

        taskStateService.upsertStep(task, new TaskStepStatus(
                orderOf(task, request.formCode()),
                request.formCode(),
                schema.formName(),
                "DRAFT_CREATED",
                draftNo,
                null,
                outputData,
                null
        ));
        task.setStatus("IN_PROGRESS");
        task.setCurrentStep("已建立「" + schema.formName() + "」草稿");
        task.setNextAction(nextActionAfterDraft(task, request.formCode()));

        return new CreateFormDraftResponse(
                true,
                request.formCode(),
                schema.formName(),
                "DRAFT_CREATED",
                draftNo,
                task.getDashboardUrl(),
                "/screenshots/" + draftNo + ".png",
                outputData,
                null
        );
    }

    public TaskStatusResponse getTaskStatus(String taskId) {
        return taskStateService.toStatusResponse(taskStateService.getTask(taskId));
    }

    private PlannedForm toPlannedForm(ProcessStep step, TaskInstance task) {
        FormRegistryEntry registry = knowledgeService.formRegistry(step.formCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown formCode in registry: " + step.formCode()));
        String status = task.getStepsByFormCode().containsKey(step.formCode())
                ? task.getStepsByFormCode().get(step.formCode()).status()
                : step.statusWhenPlanned();
        return new PlannedForm(
                step.order(),
                step.formCode(),
                step.formName(),
                status,
                registry.riskLevel(),
                registry.executionMode()
        );
    }

    private List<MissingField> missingFields(FormSchema schema, Map<String, Object> input) {
        List<MissingField> missingFields = new ArrayList<>();
        for (FieldDefinition field : schema.requiredFields()) {
            if (!isRequired(field, input)) {
                continue;
            }
            Object value = input.get(field.name());
            if (value == null || (value instanceof String text && text.isBlank())) {
                missingFields.add(new MissingField(field.name(), field.label(), field.type(), "REQUIRED"));
            }
        }
        return missingFields;
    }

    private boolean isRequired(FieldDefinition field, Map<String, Object> input) {
        return field.requiredWhen() == null || field.requiredWhen().isBlank() || conditionEvaluator.evaluate(field.requiredWhen(), input);
    }

    private Map<String, Object> mergedInput(TaskInstance task, Map<String, Object> input) {
        Map<String, Object> merged = new LinkedHashMap<>(task.getContext());
        if (input != null) {
            merged.putAll(input);
        }
        return merged;
    }

    private String firstReadyForm(List<PlannedForm> forms) {
        return forms.stream()
                .filter(form -> "READY".equals(form.status()))
                .map(form -> "目前第一步是「" + form.formName() + "」")
                .findFirst()
                .orElse("目前沒有可立即執行的表單");
    }

    private int orderOf(TaskInstance task, String formCode) {
        ProcessTemplate process = knowledgeService.processByCode(task.getProcessCode());
        return process.steps().stream()
                .filter(step -> step.formCode().equals(formCode))
                .findFirst()
                .map(ProcessStep::order)
                .orElse(0);
    }

    private String nextActionAfterDraft(TaskInstance task, String formCode) {
        ProcessTemplate process = knowledgeService.processByCode(task.getProcessCode());
        return process.steps().stream()
                .filter(step -> step.formCode().equals(formCode))
                .findFirst()
                .map(step -> "等待人工確認「" + step.formName() + "」草稿；確認後再送出。")
                .orElse("查看 Dashboard 確認下一步");
    }

    private String draftNo(String formCode) {
        return formCode.replace("_", "-") + "-DRAFT-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-001";
    }
}

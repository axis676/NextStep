package com.nextstep.formops.service;

import com.nextstep.formops.config.FormOpsProperties;
import com.nextstep.formops.model.CreateTaskRequest;
import com.nextstep.formops.model.TaskInstance;
import com.nextstep.formops.model.TaskStatusResponse;
import com.nextstep.formops.model.TaskStepStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskStateService {
    private final ProcessKnowledgeService knowledgeService;
    private final FormOpsProperties properties;
    private final Map<String, TaskInstance> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    public TaskStateService(ProcessKnowledgeService knowledgeService, FormOpsProperties properties) {
        this.knowledgeService = knowledgeService;
        this.properties = properties;
    }

    public TaskInstance createTask(CreateTaskRequest request) {
        String processCode = knowledgeService.defaultProcess().processCode();
        String taskId = nextTaskId();
        String dashboardUrl = properties.dashboardBaseUrl() + "/" + taskId;
        TaskInstance task = new TaskInstance(
                taskId,
                request.requester(),
                request.goal(),
                processCode,
                dashboardUrl,
                request.context() == null ? Map.of() : request.context()
        );
        task.setCurrentStep("尚未規劃表單流程");
        task.setNextAction("呼叫 plan_forms 產生表單流程");
        tasks.put(taskId, task);
        return task;
    }

    public TaskInstance getTask(String taskId) {
        TaskInstance task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Unknown taskId: " + taskId);
        }
        return task;
    }

    public void mergeContext(TaskInstance task, Map<String, Object> input) {
        if (input != null) {
            task.getContext().putAll(input);
        }
    }

    public void upsertStep(TaskInstance task, TaskStepStatus stepStatus) {
        task.getStepsByFormCode().put(stepStatus.formCode(), stepStatus);
    }

    public TaskStatusResponse toStatusResponse(TaskInstance task) {
        return new TaskStatusResponse(
                task.getTaskId(),
                task.getGoal(),
                task.getProcessCode(),
                task.getCurrentStep(),
                task.getStatus(),
                new ArrayList<>(task.getStepsByFormCode().values()),
                task.getNextAction()
        );
    }

    public Map<String, Object> mockOutputFor(String formCode) {
        Map<String, Object> output = new LinkedHashMap<>();
        if ("SYS_REQ".equals(formCode)) {
            output.put("systemCode", "SYS-APLY");
            output.put("sysReqFormNo", "SYS-REQ-" + today() + "-001");
        } else if ("VM_REQ".equals(formCode)) {
            output.put("hostIP", "10.10.20.15");
            output.put("vmFormNo", "VM-REQ-" + today() + "-001");
        } else if ("FIREWALL_REQ".equals(formCode)) {
            output.put("firewallFormNo", "FW-REQ-" + today() + "-001");
        }
        return output;
    }

    private String nextTaskId() {
        return "TASK-" + today() + "-" + String.format("%03d", sequence.getAndIncrement());
    }

    private String today() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}

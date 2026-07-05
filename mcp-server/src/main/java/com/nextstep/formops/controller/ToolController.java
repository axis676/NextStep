package com.nextstep.formops.controller;

import com.nextstep.formops.model.CheckMissingFieldsRequest;
import com.nextstep.formops.model.CheckMissingFieldsResponse;
import com.nextstep.formops.model.CreateFormDraftRequest;
import com.nextstep.formops.model.CreateFormDraftResponse;
import com.nextstep.formops.model.CreateTaskRequest;
import com.nextstep.formops.model.CreateTaskResponse;
import com.nextstep.formops.model.PlanFormsRequest;
import com.nextstep.formops.model.PlanFormsResponse;
import com.nextstep.formops.model.TaskStatusResponse;
import com.nextstep.formops.service.ToolService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tools")
public class ToolController {
    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    @PostMapping("/create_task")
    public CreateTaskResponse createTask(@Valid @RequestBody CreateTaskRequest request) {
        return toolService.createTask(request);
    }

    @PostMapping("/plan_forms")
    public PlanFormsResponse planForms(@Valid @RequestBody PlanFormsRequest request) {
        return toolService.planForms(request);
    }

    @PostMapping("/check_missing_fields")
    public CheckMissingFieldsResponse checkMissingFields(@Valid @RequestBody CheckMissingFieldsRequest request) {
        return toolService.checkMissingFields(request);
    }

    @PostMapping("/create_form_draft")
    public CreateFormDraftResponse createFormDraft(@Valid @RequestBody CreateFormDraftRequest request) {
        return toolService.createFormDraft(request);
    }

    @GetMapping("/get_task_status/{taskId}")
    public TaskStatusResponse getTaskStatus(@PathVariable String taskId) {
        return toolService.getTaskStatus(taskId);
    }
}

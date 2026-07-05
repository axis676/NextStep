package com.nextstep.formops.model;

import java.util.List;

public record TaskStatusResponse(
        String taskId,
        String goal,
        String processCode,
        String currentStep,
        String status,
        List<TaskStepStatus> forms,
        String nextAction
) {
}

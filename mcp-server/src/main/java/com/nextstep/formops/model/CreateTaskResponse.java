package com.nextstep.formops.model;

public record CreateTaskResponse(
        String taskId,
        String dashboardUrl
) {
}

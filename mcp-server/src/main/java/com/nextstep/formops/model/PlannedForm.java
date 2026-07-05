package com.nextstep.formops.model;

public record PlannedForm(
        int order,
        String formCode,
        String formName,
        String status,
        String riskLevel,
        String executionMode
) {
}

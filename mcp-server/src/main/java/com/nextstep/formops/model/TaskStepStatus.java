package com.nextstep.formops.model;

import java.util.Map;

public record TaskStepStatus(
        int order,
        String formCode,
        String formName,
        String status,
        String draftNo,
        String formNo,
        Map<String, Object> outputData,
        String errorReason
) {
}

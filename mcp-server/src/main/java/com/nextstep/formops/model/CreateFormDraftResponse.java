package com.nextstep.formops.model;

import java.util.Map;

public record CreateFormDraftResponse(
        boolean success,
        String formCode,
        String formName,
        String status,
        String draftNo,
        String dashboardUrl,
        String screenshotUrl,
        Map<String, Object> outputData,
        String errorReason
) {
}

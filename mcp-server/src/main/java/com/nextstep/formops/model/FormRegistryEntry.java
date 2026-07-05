package com.nextstep.formops.model;

import java.util.List;

public record FormRegistryEntry(
        String formCode,
        String formName,
        String toolName,
        String riskLevel,
        String executionMode,
        List<String> allowedActions,
        String description
) {
}

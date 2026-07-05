package com.nextstep.formops.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record CheckMissingFieldsRequest(
        @NotBlank String taskId,
        @NotBlank String formCode,
        Map<String, Object> input
) {
}

package com.nextstep.formops.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record CreateTaskRequest(
        @NotBlank String goal,
        @NotBlank String requester,
        Map<String, Object> context
) {
}

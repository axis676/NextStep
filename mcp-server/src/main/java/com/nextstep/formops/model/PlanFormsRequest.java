package com.nextstep.formops.model;

import jakarta.validation.constraints.NotBlank;

public record PlanFormsRequest(
        @NotBlank String taskId
) {
}

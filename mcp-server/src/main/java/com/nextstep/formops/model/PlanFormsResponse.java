package com.nextstep.formops.model;

import java.util.List;

public record PlanFormsResponse(
        String process,
        String processCode,
        List<PlannedForm> forms
) {
}

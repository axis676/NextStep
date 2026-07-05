package com.nextstep.formops.model;

import java.util.List;

public record ProcessTemplate(
        String processCode,
        String processName,
        String description,
        String intent,
        String defaultNextAction,
        List<ProcessStep> steps
) {
}

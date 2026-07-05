package com.nextstep.formops.model;

import java.util.List;

public record ProcessStep(
        int order,
        String formCode,
        String formName,
        String statusWhenPlanned,
        List<String> conditions,
        List<String> preconditions,
        List<String> requiredInputs,
        List<String> outputFields,
        String nextActionAfterApproved
) {
}

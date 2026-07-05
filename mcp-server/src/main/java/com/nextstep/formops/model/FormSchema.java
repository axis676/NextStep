package com.nextstep.formops.model;

import java.util.List;

public record FormSchema(
        String formName,
        List<FieldDefinition> requiredFields,
        List<FieldDefinition> outputFields
) {
}

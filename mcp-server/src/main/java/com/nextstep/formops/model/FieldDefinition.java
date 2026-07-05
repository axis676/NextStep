package com.nextstep.formops.model;

import java.util.List;

public record FieldDefinition(
        String name,
        String type,
        String label,
        String requiredWhen,
        List<String> allowedValues,
        Integer minimum,
        Integer maximum
) {
}

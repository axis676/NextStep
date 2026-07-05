package com.nextstep.formops.model;

public record MissingField(
        String name,
        String label,
        String type,
        String reason
) {
}

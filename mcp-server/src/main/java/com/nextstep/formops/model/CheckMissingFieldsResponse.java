package com.nextstep.formops.model;

import java.util.List;

public record CheckMissingFieldsResponse(
        String formCode,
        String formName,
        List<MissingField> missingFields
) {
}

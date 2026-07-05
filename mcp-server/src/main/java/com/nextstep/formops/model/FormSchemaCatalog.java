package com.nextstep.formops.model;

import java.util.Map;

public record FormSchemaCatalog(
        String version,
        Map<String, FormSchema> forms
) {
}

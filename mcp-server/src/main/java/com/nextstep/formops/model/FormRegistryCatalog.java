package com.nextstep.formops.model;

import java.util.List;

public record FormRegistryCatalog(
        String version,
        List<FormRegistryEntry> forms
) {
}

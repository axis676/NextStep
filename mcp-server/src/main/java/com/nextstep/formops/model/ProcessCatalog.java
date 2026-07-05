package com.nextstep.formops.model;

import java.util.List;

public record ProcessCatalog(
        String version,
        List<ProcessTemplate> processes
) {
}

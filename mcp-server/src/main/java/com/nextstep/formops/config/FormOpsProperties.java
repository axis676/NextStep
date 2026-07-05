package com.nextstep.formops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "formops")
public record FormOpsProperties(
        String dashboardBaseUrl,
        String configDir
) {
    public FormOpsProperties {
        if (dashboardBaseUrl == null || dashboardBaseUrl.isBlank()) {
            dashboardBaseUrl = "http://localhost:8080/dashboard/tasks";
        }
        if (configDir == null || configDir.isBlank()) {
            configDir = "../config";
        }
    }
}

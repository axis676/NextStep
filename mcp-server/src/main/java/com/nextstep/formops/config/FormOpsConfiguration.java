package com.nextstep.formops.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FormOpsProperties.class)
public class FormOpsConfiguration {
}

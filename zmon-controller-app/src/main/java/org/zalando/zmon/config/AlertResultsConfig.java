package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "zmon.alert-results")
public class AlertResultsConfig {

    private List<String> allowedFilters = Arrays.asList("application");

    public List<String> getAllowedFilters() {
        return allowedFilters;
    }

    public void setAllowedFilters(List<String> allowedFilters) {
        this.allowedFilters = allowedFilters;
    }
}

package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "zmon.alert-results")
public class AlertResultsConfig {

    private List<String> allowedFilters = new ArrayList<>(Collections.singletonList("application"));

    public List<String> getAllowedFilters() {
        return allowedFilters;
    }

    public void setAllowedFilters(List<String> allowedFilters) {
        this.allowedFilters = allowedFilters;
    }
}

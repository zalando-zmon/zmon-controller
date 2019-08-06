package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "zmon.rest")
public class RestConfiguration {
    private GetAlertResults getAlertResults = new GetAlertResults();

    public GetAlertResults getGetAlertResults() {
        return getAlertResults;
    }

    public void setGetAlertResults(GetAlertResults getAlertResults) {
        this.getAlertResults = getAlertResults;
    }

    public static class GetAlertResults {
        private List<String> allowedFilters = Arrays.asList("application");

        public List<String> getAllowedFilters() {
            return allowedFilters;
        }

        public void setAllowedFilters(List<String> allowedFilters) {
            this.allowedFilters = allowedFilters;
        }
    }
}

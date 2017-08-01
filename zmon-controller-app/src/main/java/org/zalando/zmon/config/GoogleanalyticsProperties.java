package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by elauria on 17.05.17.
 */
@ConfigurationProperties(prefix = "zmon.googleanalytics")
public class GoogleanalyticsProperties {
    public String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

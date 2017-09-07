package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by elauria on 07.09.17.
 */
@ConfigurationProperties(prefix = "zmon.instana")
public class InstanaProperties {
    public String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

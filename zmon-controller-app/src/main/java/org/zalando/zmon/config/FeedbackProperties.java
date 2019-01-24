package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.feedback")
public class FeedbackProperties {
    private String url;

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }
}

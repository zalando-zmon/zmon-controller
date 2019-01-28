package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.feedback")
public class FeedbackMetadataProperties {
    private String url;

    public Boolean feedbackEnabled = false;

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }
}

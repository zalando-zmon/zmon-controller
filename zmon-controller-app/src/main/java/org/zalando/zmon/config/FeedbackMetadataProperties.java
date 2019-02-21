package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.feedback")
public class FeedbackMetadataProperties {
    public String url;

    public boolean enableFeedback;

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }

    public boolean getEnableFeedback() { return enableFeedback; }

    public void setEnableFeedback(boolean enableFeedback) { this.enableFeedback = enableFeedback; }
}

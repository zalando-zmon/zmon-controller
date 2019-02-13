package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.feedback")
public class FeedbackMetadataProperties {
    private String url;

    public boolean feedbackEnabled = false;

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }

    public boolean getFeedbackEnabled() { return feedbackEnabled; }

    public void setFeedbackEnabled(boolean feedbackEnabled) { this.feedbackEnabled = feedbackEnabled; }
}

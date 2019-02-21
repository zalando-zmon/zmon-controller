package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.feedback")
public class FeedbackMetadataProperties {
    private String url;

    private boolean enabled;

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }

    public boolean getEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

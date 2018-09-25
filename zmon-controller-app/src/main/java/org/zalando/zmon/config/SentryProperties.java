package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentry")
public class SentryProperties {

    private String dsnUrl;
    private String sampleRate;
    private String environment;
    private String servername;
    private String tags;


    public String getDsnUrl() {
        return dsnUrl;
    }

    public void setDsnUrl(String dsnUrl) {
        this.dsnUrl = dsnUrl;
    }

    public String getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(String sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}

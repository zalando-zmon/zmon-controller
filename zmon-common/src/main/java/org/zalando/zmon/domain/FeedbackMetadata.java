package org.zalando.zmon.domain;

public class FeedbackMetadata {

    private String url;

    public FeedbackMetadata(String url) {
        this.url = url;
    }

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }
}

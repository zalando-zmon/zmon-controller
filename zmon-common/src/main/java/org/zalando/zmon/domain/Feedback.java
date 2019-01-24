package org.zalando.zmon.domain;

public class Feedback {

    private String url;

    public Feedback(String url) {
        this.url = url;
    }

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }
}

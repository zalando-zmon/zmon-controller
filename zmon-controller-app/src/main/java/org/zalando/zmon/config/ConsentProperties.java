package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by elauria on 17.05.17.
 */
@ConfigurationProperties(prefix = "zmon.consent")
public class ConsentProperties {
    public String title;
    public String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

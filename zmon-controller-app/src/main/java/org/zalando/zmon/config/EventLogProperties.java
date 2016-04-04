package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;

/**
 * Created by hjacobs on 1/28/16.
 */
@ConfigurationProperties(prefix = "zmon.eventlog")
public class EventLogProperties {

    private URL url;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}

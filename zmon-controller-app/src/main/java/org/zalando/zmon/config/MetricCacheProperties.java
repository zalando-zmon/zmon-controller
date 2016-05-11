package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;

/**
 * Created by hjacobs on 2/5/16.
 */
@ConfigurationProperties(prefix = "zmon.metriccache")
public class MetricCacheProperties {

    private URL url;
    private int nodes = 3;

    public int getNodes() {
        return nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}

package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by jmussler on 26.02.16.
 */
@ConfigurationProperties(prefix = "zmon")
public class ControllerProperties {

    public String staticUrl = "";
    public int grafanaMinInterval;
    public String KairosDBBaseUrl = "";

    public String getKairosDBBaseUrl() {
        return KairosDBBaseUrl;
    }

    public void setKairosDBBaseUrl(String kairosDBBaseUrl) {
        KairosDBBaseUrl = kairosDBBaseUrl;
    }

    public String getStaticUrl() {
        return staticUrl;
    }

    public void setStaticUrl(String staticUrl) {
        this.staticUrl = staticUrl;
    }

    public int getGrafanaMinInterval() {
        return grafanaMinInterval;
    }

    public void setGrafanaMinInterval(int grafanaMinInterval) {
        this.grafanaMinInterval = grafanaMinInterval;
    }
}

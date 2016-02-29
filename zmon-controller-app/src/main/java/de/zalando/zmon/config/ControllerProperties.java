package de.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by jmussler on 26.02.16.
 */
@ConfigurationProperties(prefix = "zmon")
public class ControllerProperties {
    public int grafanaMinInterval;

    public int getGrafanaMinInterval() {
        return grafanaMinInterval;
    }

    public void setGrafanaMinInterval(int grafanaMinInterval) {
        this.grafanaMinInterval = grafanaMinInterval;
    }
}

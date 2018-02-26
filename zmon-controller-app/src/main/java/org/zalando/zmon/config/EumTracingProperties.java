package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opentracing.eum")
public class EumTracingProperties {

    public String zmonConfig;
    public String grafanaConfig;

    public String getZmonConfig() {
        return zmonConfig;
    }

    public void setZmonConfig(String zmonConfig) {
        this.zmonConfig = zmonConfig;
    }

    public String getGrafanaConfig() {
        return grafanaConfig;
    }

    public void setGrafanaConfig(String grafanaConfig) {
        this.grafanaConfig = grafanaConfig;
    }
}

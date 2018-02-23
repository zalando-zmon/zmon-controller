package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opentracing.eum")
public class EumTracingZmonProperties {

    private String zmonConfig = "localhost";

    public String getZmonConfig() {
        return zmonConfig;
    }

    public void setZmonConfig(final String zmonConfig) {
        this.zmonConfig = zmonConfig;
    }

    private String grafanaConfig = "localhost";

    public String getGrafanaConfig() {
        return grafanaConfig;
    }

    public void setGrafanaConfig(String grafanaConfig) {
        this.grafanaConfig = grafanaConfig;
    }
}

package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = "opentracing.eum")
public class EumTracingProperties {

    public Map<String, Object> zmonConfig;
    public Map<String, Object> grafanaConfig;

    public Map<String, Object> getZmonConfig() {
        return zmonConfig;
    }

    public void setZmonConfig(Map<String, Object> zmonConfig) { this.zmonConfig = zmonConfig; }

    public Map<String, Object> getGrafanaConfig() {
        return grafanaConfig;
    }

    public void setGrafanaConfig(Map<String, Object> grafanaConfig) {
        this.grafanaConfig = grafanaConfig;
    }
}

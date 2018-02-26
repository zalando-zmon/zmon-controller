package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opentracing.eum")
public class EumTracingProperties {

    public String zmonConfig = "{ name: \"noop\", config: {accessToken: \"123\", eumCollectorHost: \"localhost\", eumCollectorPort: 443, eumServiceName: \"zmon-controller-ui\", eumCollectorEncryption: 'tls'} }";

    public String getZmonConfig() {
        return zmonConfig;
    }

    public void setZmonConfig(final String zmonConfig) {
        this.zmonConfig = zmonConfig;
    }

    public String grafanaConfig = "{ name: \"noop\", config: {accessToken: \"123\", eumCollectorHost: \"localhost\", eumCollectorPort: 443, eumServiceName: \"zmon-grafana-ui\", eumVerbosity: 1, eumXHR: \"true\" eumCollectorEncryption: 'tls'} }";

    public String getGrafanaConfig() {
        return grafanaConfig;
    }

    public void setGrafanaConfig(String grafanaConfig) {
        this.grafanaConfig = grafanaConfig;
    }
}

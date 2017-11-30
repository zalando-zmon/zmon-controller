package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.lightstep")
public class LightstepProperties {
    public String accessToken;
    public String collectorHost;
    public Integer collectorPort;
    public String collectorProtocol="https";
    public String componentName="zmon-controller";

    public String getCollectorProtocol() {
        return collectorProtocol;
    }

    public void setCollectorProtocol(String collectorProtocol) {
        this.collectorProtocol = collectorProtocol;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCollectorHost() {
        return collectorHost;
    }

    public void setCollectorHost(String collectorHost) {
        this.collectorHost = collectorHost;
    }

    public Integer getCollectorPort() {
        return collectorPort;
    }

    public void setCollectorPort(Integer collectorPort) {
        this.collectorPort = collectorPort;
    }
}

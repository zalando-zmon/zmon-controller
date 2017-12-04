package org.zalando.zauth.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.lightstep")
public class LightstepProperties {
    public String accessToken;
    public String collectorHost;
    public Integer collectorPort;
    public String collectorProtocol = "https";
    public String eumCollectorHost;
    public Integer eumCollectorPort;
    public String componentName = "zmon-controller";

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

    public String getEumCollectorHost() {
        return eumCollectorHost;
    }

    public void setEumCollectorHost(String eumCollectorHost) {
        this.eumCollectorHost = eumCollectorHost;
    }

    public Integer getEumCollectorPort() {
        return eumCollectorPort;
    }

    public void setEumCollectorPort(Integer eumCollectorPort) {
        this.eumCollectorPort = eumCollectorPort;
    }
}

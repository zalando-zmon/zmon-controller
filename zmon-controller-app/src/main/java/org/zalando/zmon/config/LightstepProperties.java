package org.zalando.zmon.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "tracer")
public class LightstepProperties {

    private String accessToken;
    private String collectorHost;
    private int collectorPort;
    private String collectorProtocol;
    private String componentName;

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

    public int getCollectorPort() {
        return collectorPort;
    }

    public void setCollectorPort(int collectorPort) {
        this.collectorPort = collectorPort;
    }

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

    public String toString(){
        return this.accessToken + this.collectorHost + this.collectorProtocol + this.componentName + this.collectorPort;
    }
}
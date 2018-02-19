package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opentracing.tracer")
public class LightstepProperties {
    public String accessToken;
    public String collectorHost;
    public Integer collectorPort;
    public String collectorProtocol="https";
    public String componentName="zmon-controller";
    public String eumCollectorHost;
    public Integer eumCollectorPort;
    public String eumCollectorEncryption="tls";
    public String eumComponentName="zmon-controller-ui";
    public String eumGrafanaComponentName="zmon-grafana-ui";
    public Integer eumVerbosity=1;

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

    public String getEumCollectorEncryption() {
        return eumCollectorEncryption;
    }

    public void setEumCollectorEncryption(String eumCollectorEncryption) {
        this.eumCollectorEncryption = eumCollectorEncryption;
    }

    public String getEumComponentName() {
        return eumComponentName;
    }

    public void setEumComponentName(String eumComponentName) {
        this.eumComponentName = eumComponentName;
    }

    public String getEumGrafanaComponentName() {
        return eumGrafanaComponentName;
    }

    public void setEumGrafanaComponentName(String eumGrafanaComponentName) {
        this.eumGrafanaComponentName = eumGrafanaComponentName;
    }

    public Integer getEumVerbosity() {
        return eumVerbosity;
    }

    public void setEumVerbosity(Integer eumVerbosity) {
        this.eumVerbosity = eumVerbosity;
    }
}

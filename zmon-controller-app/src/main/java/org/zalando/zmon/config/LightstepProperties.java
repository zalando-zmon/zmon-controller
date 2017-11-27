package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zmon.lightstep")
public class LightstepProperties {
    public String accessToken;
    public String componentName;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAcessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}

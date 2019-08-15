package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.zalando.zmon.domain.DefinitionRuntime;

@Configuration
@ConfigurationProperties(prefix = "zmon.checkruntime")
public class CheckRuntimeConfig {
    private boolean enabled = false;
    private DefinitionRuntime defaultRuntime = DefinitionRuntime.PYTHON_2;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DefinitionRuntime getDefaultRuntime() {
        return defaultRuntime;
    }

    public void setDefaultRuntime(DefinitionRuntime defaultRuntime) {
        this.defaultRuntime = defaultRuntime;
    }
}

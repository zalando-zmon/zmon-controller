package org.zalando.zmon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.zalando.zmon.domain.DefinitionRuntime;

@Configuration
@ConfigurationProperties(prefix = "zmon.checkruntime")
public class CheckRuntimeConfig {
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DefinitionRuntime getDefaultRuntime() {
        return enabled ? DefinitionRuntime.PYTHON_3 : DefinitionRuntime.PYTHON_2;
    }
}

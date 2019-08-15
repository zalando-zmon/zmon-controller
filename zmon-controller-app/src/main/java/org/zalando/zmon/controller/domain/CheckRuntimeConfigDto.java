package org.zalando.zmon.controller.domain;

import org.zalando.zmon.config.CheckRuntimeConfig;
import org.zalando.zmon.domain.DefinitionRuntime;

import java.util.List;
import java.util.Map;

public class CheckRuntimeConfigDto {
    private boolean enabled;
    private DefinitionRuntime defaultRuntime;
    private Map<DefinitionRuntime, String> runtimeLabels;

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

    public Map<DefinitionRuntime, String> getRuntimeLabels() {
        return runtimeLabels;
    }

    public void setRuntimeLabels(Map<DefinitionRuntime, String> runtimeLabels) {
        this.runtimeLabels = runtimeLabels;
    }

    public static CheckRuntimeConfigDto createFromCheckRuntimeConfig(CheckRuntimeConfig config) {
        CheckRuntimeConfigDto dto = new CheckRuntimeConfigDto();
        dto.setEnabled(config.isEnabled());
        dto.setDefaultRuntime(config.getDefaultRuntime());
        dto.setRuntimeLabels(DefinitionRuntime.labeledValues());

        return dto;
    }
}
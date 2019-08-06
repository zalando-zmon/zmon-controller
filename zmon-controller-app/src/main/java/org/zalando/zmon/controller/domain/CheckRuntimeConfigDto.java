package org.zalando.zmon.controller.domain;

import org.zalando.zmon.config.CheckRuntimeConfig;
import org.zalando.zmon.domain.DefinitionRuntime;

import java.util.List;
import java.util.Map;

public class CheckRuntimeConfigDto {
    private Boolean enabled;
    private Map<String, String> defaultRuntime;
    private List<Map<String, String>> allowedRuntimesForCreate;
    private List<Map<String, String>> allowedRuntimesForUpdate;

    public static CheckRuntimeConfigDto createFromCheckRuntimeConfig(CheckRuntimeConfig config) {
        CheckRuntimeConfigDto dto = new CheckRuntimeConfigDto();
        dto.setEnabled(config.isEnabled());
        dto.setDefaultRuntime(DefinitionRuntime.asMap(config.getDefaultRuntime()));
        dto.setAllowedRuntimesForCreate(DefinitionRuntime.asListOfMaps(config.getAllowedRuntimesForCreate()));
        dto.setAllowedRuntimesForUpdate(DefinitionRuntime.asListOfMaps(config.getAllowedRuntimesForUpdate()));

        return dto;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getDefaultRuntime() {
        return defaultRuntime;
    }

    public void setDefaultRuntime(Map<String, String> defaultRuntime) {
        this.defaultRuntime = defaultRuntime;
    }

    public List<Map<String, String>> getAllowedRuntimesForCreate() {
        return allowedRuntimesForCreate;
    }

    public void setAllowedRuntimesForCreate(List<Map<String, String>> allowedRuntimesForCreate) {
        this.allowedRuntimesForCreate = allowedRuntimesForCreate;
    }

    public List<Map<String, String>> getAllowedRuntimesForUpdate() {
        return allowedRuntimesForUpdate;
    }

    public void setAllowedRuntimesForUpdate(List<Map<String, String>> allowedRuntimesForUpdate) {
        this.allowedRuntimesForUpdate = allowedRuntimesForUpdate;
    }
}
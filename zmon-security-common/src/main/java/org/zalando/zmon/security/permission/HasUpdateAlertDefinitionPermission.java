package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.security.authority.ZMonAuthority;

class HasUpdateAlertDefinitionPermission extends AlertDefinitionPermission {

    private AlertDefinition newAlertDefinition;

    HasUpdateAlertDefinitionPermission(AlertDefinition currentAlertDefinition, AlertDefinition newAlertDefinition) {
        super(currentAlertDefinition);
        this.newAlertDefinition = newAlertDefinition;
    }

    @Override
    public Boolean apply(ZMonAuthority input) {
        return input.hasUpdateAlertDefinitionPermission(alertDefinition, newAlertDefinition);
    }

}

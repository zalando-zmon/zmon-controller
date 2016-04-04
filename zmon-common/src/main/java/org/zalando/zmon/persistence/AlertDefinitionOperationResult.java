package org.zalando.zmon.persistence;

import org.zalando.zmon.domain.AlertDefinition;

import de.zalando.typemapper.annotations.DatabaseField;

public class AlertDefinitionOperationResult extends OperationResult {

    @DatabaseField
    private AlertDefinition entity;

    public AlertDefinition getEntity() {
        return entity;
    }

    public void setEntity(final AlertDefinition entity) {
        this.entity = entity;
    }
}

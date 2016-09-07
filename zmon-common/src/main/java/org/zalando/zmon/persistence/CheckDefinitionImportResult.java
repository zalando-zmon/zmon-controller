package org.zalando.zmon.persistence;

import de.zalando.typemapper.annotations.DatabaseType;
import org.zalando.zmon.domain.CheckDefinition;

import de.zalando.typemapper.annotations.DatabaseField;

@DatabaseType
public class CheckDefinitionImportResult {

    @DatabaseField
    private CheckDefinition entity;

    @DatabaseField
    private boolean newEntity;

    @DatabaseField
    private boolean permissionDenied;

    public CheckDefinition getEntity() {
        return entity;
    }

    public void setEntity(final CheckDefinition entity) {
        this.entity = entity;
    }

    public boolean isNewEntity() {
        return newEntity;
    }

    public void setNewEntity(final boolean newEntity) {
        this.newEntity = newEntity;
    }

    public boolean isPermissionDenied() {
        return permissionDenied;
    }

    public void setPermissionDenied(boolean permissionDenied) {
        this.permissionDenied = permissionDenied;
    }
}

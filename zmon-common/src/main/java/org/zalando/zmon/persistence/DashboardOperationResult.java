package org.zalando.zmon.persistence;

import org.zalando.zmon.domain.DashboardImport;

import de.zalando.typemapper.annotations.DatabaseField;

/**
 * @author  danieldelhoyo daniel.del.hoyo AT zalando DOT org
 */
public class DashboardOperationResult extends OperationResult {

    @DatabaseField
    private DashboardImport entity;

    public DashboardImport getEntity() {
        return entity;
    }

    public void setEntity(final DashboardImport entity) {
        this.entity = entity;
    }
}

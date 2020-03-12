package org.zalando.zmon.persistence;

import org.zalando.zmon.domain.DashboardRecord;

import de.zalando.typemapper.annotations.DatabaseField;

/**
 * @author  danieldelhoyo daniel.del.hoyo AT zalando DOT org
 */
public class DashboardOperationResult extends OperationResult {

    @DatabaseField
    private DashboardRecord entity;

    public DashboardRecord getEntity() {
        return entity;
    }

    public void setEntity(final DashboardRecord entity) {
        this.entity = entity;
    }
}

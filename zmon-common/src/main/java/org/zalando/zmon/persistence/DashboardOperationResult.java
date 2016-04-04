package org.zalando.zmon.persistence;

import org.zalando.zmon.domain.Dashboard;

import de.zalando.typemapper.annotations.DatabaseField;

/**
 * @author  danieldelhoyo daniel.del.hoyo AT zalando DOT org
 */
public class DashboardOperationResult extends OperationResult {

    @DatabaseField
    private Dashboard entity;

    public Dashboard getEntity() {
        return entity;
    }

    public void setEntity(final Dashboard entity) {
        this.entity = entity;
    }
}

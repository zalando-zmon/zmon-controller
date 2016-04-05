package org.zalando.zmon.persistence;

import org.zalando.zmon.domain.AlertComment;
import org.zalando.zmon.exception.ZMonException;

import de.zalando.typemapper.annotations.DatabaseField;

public class AlertCommentOperationResult extends OperationResult {

    @DatabaseField
    private AlertComment entity;

    public AlertComment getEntity() throws ZMonException {
        throwExceptionOnFailure();
        return entity;
    }

    public void setEntity(final AlertComment entity) {
        this.entity = entity;
    }

}

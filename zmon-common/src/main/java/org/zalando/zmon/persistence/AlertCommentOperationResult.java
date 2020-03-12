package org.zalando.zmon.persistence;

import org.zalando.zmon.domain.AlertCommentRecord;
import org.zalando.zmon.exception.ZMonException;

import de.zalando.typemapper.annotations.DatabaseField;

public class AlertCommentOperationResult extends OperationResult {

    @DatabaseField
    private AlertCommentRecord entity;

    public AlertCommentRecord getEntity() throws ZMonException {
        throwExceptionOnFailure();
        return entity;
    }

    public void setEntity(final AlertCommentRecord entity) {
        this.entity = entity;
    }

}

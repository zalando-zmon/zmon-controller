package org.zalando.zmon.persistence;

import org.zalando.zmon.domain.AlertCommentImport;
import org.zalando.zmon.exception.ZMonException;

import de.zalando.typemapper.annotations.DatabaseField;

public class AlertCommentOperationResult extends OperationResult {

    @DatabaseField
    private AlertCommentImport entity;

    public AlertCommentImport getEntity() throws ZMonException {
        throwExceptionOnFailure();
        return entity;
    }

    public void setEntity(final AlertCommentImport entity) {
        this.entity = entity;
    }

}

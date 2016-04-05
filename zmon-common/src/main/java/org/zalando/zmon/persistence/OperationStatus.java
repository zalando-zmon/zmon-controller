package org.zalando.zmon.persistence;

import org.zalando.zmon.exception.AlertDefinitionFieldMissingException;
import org.zalando.zmon.exception.AlertDefinitionNotFoundException;
import org.zalando.zmon.exception.CheckDefinitionNotActiveException;
import org.zalando.zmon.exception.DeleteNonLeafAlertDefinitionException;
import org.zalando.zmon.exception.ZMonExceptionFactory;

public enum OperationStatus {

    SUCCESS(null),
    ALERT_DEFINITION_NOT_FOUND(AlertDefinitionNotFoundException.FACTORY),
    CHECK_DEFINITION_NOT_ACTIVE(CheckDefinitionNotActiveException.FACTORY),
    DELETE_NON_LEAF_ALERT_DEFINITION(DeleteNonLeafAlertDefinitionException.FACTORY),
    ALERT_DEFINITION_FIELD_MISSING(AlertDefinitionFieldMissingException.FACTORY);

    private final ZMonExceptionFactory exceptionFactory;

    private OperationStatus(final ZMonExceptionFactory factory) {
        this.exceptionFactory = factory;
    }

    public ZMonExceptionFactory getExceptionFactory() {
        return exceptionFactory;
    }
}

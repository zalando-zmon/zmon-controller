package org.zalando.zmon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "Alert ID not found.")
public class AlertDefinitionNotFoundException extends ZMonException {

    public static final ZMonExceptionFactory FACTORY = new ZMonExceptionFactory() {

        @Override
        public ZMonException create(final String message) {
            return new AlertDefinitionNotFoundException(message);
        }
    };

    private static final long serialVersionUID = 1L;

    public AlertDefinitionNotFoundException(final String message) {
        super(message);
    }
}

package org.zalando.zmon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(value=HttpStatus.FORBIDDEN, reason = "Access to check denied! Check your team permissions")
public class CheckPermissionDeniedException extends ZMonException {

    public static final ZMonExceptionFactory FACTORY = new ZMonExceptionFactory() {

        @Override
        public ZMonException create(final String message) {
            return new CheckPermissionDeniedException(message);
        }
    };

    private static final long serialVersionUID = 1L;

    public CheckPermissionDeniedException(String message) { super(message);}
}

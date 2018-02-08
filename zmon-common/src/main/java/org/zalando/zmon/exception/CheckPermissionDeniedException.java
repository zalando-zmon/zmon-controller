package org.zalando.zmon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(value=HttpStatus.FORBIDDEN, reason = "Access to check denied! Check your team permissions")
public class CheckPermissionDeniedException extends ZMonException {

    private static final long serialVersionUID = 1L;

    public CheckPermissionDeniedException(String message) { super(message);}
}

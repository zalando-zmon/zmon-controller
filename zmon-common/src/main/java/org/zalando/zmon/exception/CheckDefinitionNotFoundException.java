package org.zalando.zmon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason = "Check ID not found. Try again with a valid check Id!")
public class CheckDefinitionNotFoundException extends ZMonException{

    private static final long serialVersionUID = 1L;

    public CheckDefinitionNotFoundException(String message) { super(message);}
    public CheckDefinitionNotFoundException(){}

}

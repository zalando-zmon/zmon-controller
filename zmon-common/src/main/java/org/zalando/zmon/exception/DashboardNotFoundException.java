package org.zalando.zmon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "Dashboard doesn't exsits. Please check the dashboard Id!")
public class DashboardNotFoundException extends ZMonException {

    private static final long serialVersionUID = 1L;

    public DashboardNotFoundException(String message) { super(message);}
}

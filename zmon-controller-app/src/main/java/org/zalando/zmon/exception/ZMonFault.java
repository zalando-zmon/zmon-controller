package org.zalando.zmon.exception;

import org.zalando.zmon.exception.ZMonException;

@Deprecated
public class ZMonFault extends ZMonException {

    private static final long serialVersionUID = 1L;

    public ZMonFault(final String message) {
        super(message);
    }

    public ZMonFault(final String message, final Throwable cause) {
        super(message, cause);
    }

}

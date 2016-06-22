package org.zalando.zmon.security.rememberme;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.OrderComparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

import javaslang.control.Try;

public class MultiRememberMeServices implements RememberMeServices, LogoutHandler {

    private final Logger log = LoggerFactory.getLogger(MultiRememberMeServices.class);

    private List<ZMonRememberMeServices> delegates = new LinkedList<>();

    public MultiRememberMeServices(ZMonRememberMeServices... delegates) {
        for (ZMonRememberMeServices s : delegates) {
            if (s != null) {
                this.delegates.add(s);
            }
        }
        Collections.sort(this.delegates, new OrderComparator());
        Assert.notEmpty(this.delegates, "'delegates'-list should never be empty");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        delegates.forEach(delegate -> delegate.logout(request, response, authentication));
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        for (ZMonRememberMeServices candidate : delegates) {
            Authentication auth = Try.of(() -> candidate.autoLogin(request, response)).getOrElse((Authentication) null);
            if (auth != null) {
                // inform everybody else of the successful auto-login
                // (but not ourself)
                delegates.stream()
                        .filter(delegate -> delegate != candidate)
                        .forEach(delegate -> delegate.autoLoginSuccess(request, response, auth));
                return auth;
            }
        }
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        delegates.forEach(delegate -> delegate.loginFail(request, response));
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {
        delegates.forEach(delegate -> delegate.loginSuccess(request, response, successfulAuthentication));
    }

}

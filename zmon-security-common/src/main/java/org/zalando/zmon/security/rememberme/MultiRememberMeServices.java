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

    //@formatter:off
    public MultiRememberMeServices(ZMonRememberMeServices... delegates) {
        for(ZMonRememberMeServices s : delegates){
            if(s != null){
                this.delegates.add(s);
            }
        }
        //
        Collections.sort(this.delegates, new OrderComparator());
        Assert.notEmpty(this.delegates, "'delegates'-list should never be empty");
    }
    //@formatter:on

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("delegate for 'logout' ...");
        delegates.forEach(delegate -> delegate.logout(request, response, authentication));
        log.debug("'logout' done");
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        for (ZMonRememberMeServices s : delegates) {
            Authentication a = Try.of(() -> s.autoLogin(request, response)).getOrElse((Authentication) null);
            if (a != null) {
                log.debug("return autentication : {}", a.toString());
                return a;
            }
        }
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        log.debug("delegate for 'login-Fail' ...");
        delegates.forEach(delegate -> delegate.loginFail(request, response));
        log.debug("'login-Fail' done");
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {
        log.debug("delegate for 'login-success' ...");
        delegates.forEach(delegate -> delegate.loginSuccess(request, response, successfulAuthentication));
        log.debug("'login-success' done");
    }

}

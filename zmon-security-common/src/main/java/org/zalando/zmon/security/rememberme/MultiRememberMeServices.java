package org.zalando.zmon.security.rememberme;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.OrderComparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javaslang.control.Try;

public class MultiRememberMeServices implements RememberMeServices, LogoutHandler {

    private List<ZMonRememberMeServices> delegates = new LinkedList<>();

    //@formatter:off
    public MultiRememberMeServices(ZMonRememberMeServices... delegates) {
        Arrays.asList(delegates).stream()
                                .filter(s -> Objects.nonNull(s))
                                .map(d -> this.delegates.add(d));

        //
        Collections.sort(this.delegates, new OrderComparator());
    }
    //@formatter:on

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        delegates.forEach(delegate -> delegate.logout(request, response, authentication));
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        for (ZMonRememberMeServices s : delegates) {
            Authentication a = Try.of(() -> s.autoLogin(request, response)).getOrElse((Authentication) null);
            if (a != null) {
                return a;
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

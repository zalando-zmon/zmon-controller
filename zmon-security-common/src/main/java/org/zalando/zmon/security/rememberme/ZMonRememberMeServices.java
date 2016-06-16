package org.zalando.zmon.security.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * We want to use multiple {@link RememberMeServices} in combination. Maybe that
 * works.
 * 
 * @author jbellmann
 *
 */
public interface ZMonRememberMeServices extends RememberMeServices, Ordered, LogoutHandler {

    @Override
    default Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    default void loginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {
    }

    @Override
    default void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    }

    @Override
    default int getOrder() {
        return 0;
    }

    @Override
    default void loginFail(HttpServletRequest request, HttpServletResponse response) {
    }
}

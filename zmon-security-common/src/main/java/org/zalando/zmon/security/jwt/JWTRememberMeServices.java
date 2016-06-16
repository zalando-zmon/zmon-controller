package org.zalando.zmon.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.zalando.zmon.security.rememberme.ZMonRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTRememberMeServices implements ZMonRememberMeServices {

    private final JWTService jwtService;

    private final Logger log = LoggerFactory.getLogger(JWTRememberMeServices.class);

    public JWTRememberMeServices(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        return jwtService.authenticateFromCookie(request, response);
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
                             Authentication successfulAuthentication) {
        jwtService.writeCookie(request, response, successfulAuthentication);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        jwtService.removeCookie(request, response);
    }

    @Override
    public int getOrder() {
        return ZMonRememberMeServices.super.getOrder() - 20;
    }

}

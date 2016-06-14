package org.zalando.zmon.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.zalando.zmon.security.rememberme.ZMonRememberMeServices;

/**
 * 
 * @author jbellmann
 *
 */
public class JWTRememberMeServices implements ZMonRememberMeServices {

    private final JWTService jwtService;

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

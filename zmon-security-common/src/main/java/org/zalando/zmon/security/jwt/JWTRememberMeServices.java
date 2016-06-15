package org.zalando.zmon.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.zalando.zmon.security.rememberme.ZMonRememberMeServices;

/**
 * 
 * @author jbellmann
 *
 */
public class JWTRememberMeServices implements ZMonRememberMeServices {

    private final JWTService jwtService;
    
    private final Logger log = LoggerFactory.getLogger(JWTRememberMeServices.class);

    public JWTRememberMeServices(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        log.debug("autologin with jwt ...");
        return jwtService.authenticateFromCookie(request, response);
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {
        log.debug("write cookie with jwt ...");
        jwtService.writeCookie(request, response, successfulAuthentication);
        log.debug("... cookie written");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("remove cookie with jwt ...");
        jwtService.removeCookie(request, response);
        log.debug("... cookie removed");
    }

    @Override
    public int getOrder() {
        return ZMonRememberMeServices.super.getOrder() - 20;
    }

}

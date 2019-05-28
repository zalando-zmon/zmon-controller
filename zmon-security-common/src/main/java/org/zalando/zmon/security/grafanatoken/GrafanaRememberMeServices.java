package org.zalando.zmon.security.grafanatoken;

import org.springframework.security.core.Authentication;
import org.zalando.zmon.security.rememberme.ZMonRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GrafanaRememberMeServices implements ZMonRememberMeServices {

    private final GrafanaTokenService grafanaTokenService;

    public GrafanaRememberMeServices(GrafanaTokenService grafanaTokenService) {
        this.grafanaTokenService = grafanaTokenService;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        return grafanaTokenService.authenticateFromHeader(request, response);
    }
}

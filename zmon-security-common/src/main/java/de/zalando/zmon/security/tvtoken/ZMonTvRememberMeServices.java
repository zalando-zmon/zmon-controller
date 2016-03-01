package de.zalando.zmon.security.tvtoken;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.util.WebUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import de.zalando.zmon.security.authority.ZMonViewerAuthority;

public class ZMonTvRememberMeServices implements RememberMeServices, LogoutHandler {

    private final Logger log = LoggerFactory.getLogger(ZMonTvRememberMeServices.class);

    private final TvTokenService tvTokenService;

    public ZMonTvRememberMeServices(TvTokenService tvTokenService) {
        this.tvTokenService = tvTokenService;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        log.warn("AUTO_LOGIN_CALLED : ");
        Cookie cookie = WebUtils.getCookie(request, "ZMON_TV");
        if (cookie != null) {
            log.warn("COOKIE_WAS_FOUND_COULD_TRY_LOGIN");
            List<String> values = Lists.newArrayList(tvTokenService.decodeCookieValue(cookie.getValue()));
            log.warn("FOUND_VALUES : {}", values.toString());
            List<GrantedAuthority> authorities = Lists.newArrayList(new ZMonViewerAuthority("ZMON_TV", ImmutableSet.of()));
            return new RememberMeAuthenticationToken("ZMON_TV", "ZMON_TV_" + values.get(0), authorities);
        }
        // no success
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        log.warn("LOGIN_FAILED_CALLED");
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication successfulAuthentication) {
        log.warn("LOGIN_SUCCESS_CALLED");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.warn("LOGOUT_CALLED");
    }

}

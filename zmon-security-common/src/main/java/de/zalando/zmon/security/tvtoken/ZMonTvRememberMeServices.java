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
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
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
        Cookie zmonTvToken = WebUtils.getCookie(request, TvTokenService.ZMON_TV);
        Cookie zmonIdToken = WebUtils.getCookie(request, TvTokenService.ZMON_TV_ID);
        if (zmonTvToken != null && zmonIdToken != null) {
            log.warn("COOKIES_WERE_FOUND_COULD_TRY_LOGIN");

            String token = new String(Base64Utils.decode(zmonTvToken.getValue().getBytes()));
            String id = new String(Base64Utils.decode(zmonIdToken.getValue().getBytes()));
            String ip = request.getHeader(TvTokenService.X_FORWARDED_FOR);
            if (!StringUtils.hasText(ip)) {
                ip = TvTokenService.remoteIp(request);
            }

            log.info("FOUND_VALUES - token: {}, id: {}, ip: {}", token, id, ip);
            if (tvTokenService.isValidToken(token, ip, id)) {
                List<GrantedAuthority> authorities = Lists
                        .newArrayList(new ZMonViewerAuthority("ZMON_TV", ImmutableSet.of()));
                return new RememberMeAuthenticationToken("ZMON_TV", "ZMON_TV_" + token, authorities);
            }
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
        tvTokenService.deleteCookiesIfExistent(request, response);
    }

}

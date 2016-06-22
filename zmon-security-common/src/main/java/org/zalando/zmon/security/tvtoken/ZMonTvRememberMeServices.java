package org.zalando.zmon.security.tvtoken;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;
import org.zalando.zmon.security.authority.ZMonViewerAuthority;
import org.zalando.zmon.security.rememberme.ZMonRememberMeServices;

import com.codahale.metrics.Meter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class ZMonTvRememberMeServices implements ZMonRememberMeServices {

    private final Logger log = LoggerFactory.getLogger(ZMonTvRememberMeServices.class);

    private final TvTokenService tvTokenService;

    public ZMonTvRememberMeServices(TvTokenService tvTokenService) {
        this.tvTokenService = tvTokenService;
    }

    private Meter rateLimit = new Meter();

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        Cookie zmonTvToken = WebUtils.getCookie(request, TvTokenService.ZMON_TV);
        Cookie zmonIdToken = WebUtils.getCookie(request, TvTokenService.ZMON_TV_ID);
        if (zmonTvToken != null && zmonIdToken != null) {
            String token = new String(Base64Utils.decode(zmonTvToken.getValue().getBytes()));
            String id = new String(Base64Utils.decode(zmonIdToken.getValue().getBytes()));
            String ip = request.getHeader(TvTokenService.X_FORWARDED_FOR);
            if (!StringUtils.hasText(ip)) {
                ip = TvTokenService.remoteIp(request);
            }

            rateLimit.mark();
            if (rateLimit.getOneMinuteRate() > 5) {
                // try not to hit database more than 5/sec for any given token
                return null;
            }

            if (tvTokenService.isValidToken(token, ip, id)) {
                log.info("valid token found: {}, id: {}, ip: {}", token, id, ip);
                List<GrantedAuthority> authorities = Lists
                        .newArrayList(new ZMonViewerAuthority("ZMON_TV", ImmutableSet.of()));
                return new RememberMeAuthenticationToken("ZMON_TV", "ZMON_TV_" + token, authorities);
            }
        }
        // no success
        return null;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        tvTokenService.deleteCookiesIfExistent(request, response);
    }

}

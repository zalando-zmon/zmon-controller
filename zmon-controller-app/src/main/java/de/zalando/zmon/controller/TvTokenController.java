package de.zalando.zmon.controller;

import static de.zalando.zmon.security.tvtoken.TvTokenService.X_FORWARDED_FOR;
import static java.util.concurrent.TimeUnit.DAYS;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.WebUtils;

import de.zalando.zmon.security.tvtoken.TvTokenService;

/**
 * 
 * @author jbellmann
 *
 */
@Controller
public class TvTokenController {

    private final Logger log = LoggerFactory.getLogger(TvTokenController.class);

    private final TvTokenService tvTokenService;

    @Autowired
    public TvTokenController(TvTokenService tvTokenService) {
        this.tvTokenService = tvTokenService;
    }

    @RequestMapping("/tv/{token}")
    public String handleToken(@PathVariable String token,
            @RequestHeader(name = X_FORWARDED_FOR, required = false) String bindIp,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (StringUtils.hasText(token) && token.length() > 5) {
            // check-token
            if (bindIp == null) {
                bindIp = remoteIp(request);
            }
            String bindRandom = UUID.randomUUID().toString();
            if (isValidToken(token, bindIp, bindRandom)) {
                bindZmonTvCookie(token, request, response);
                bindZmonUidCookie(bindRandom, request, response);
            } else {
                log.warn("INVALID TOKEN PASSED : {}. Delete existent Cookies.", token);
                tvTokenService.deleteCookiesIfExistent(request, response);
            }
        } else {
            log.warn("INVALID TOKEN PASSED : {}. Delete existent Cookies.", token);
            tvTokenService.deleteCookiesIfExistent(request, response);
        }
        return "redirect:/";
    }

    protected boolean isValidToken(String token, String bindIp, String sessionId) {
        return tvTokenService.isValidToken(token, bindIp, sessionId);
    }

    public static String remoteIp(HttpServletRequest request) {
        if (request != null) {
            return request.getRemoteAddr();
        }
        return "UNKNOWN";
    }

    protected void bindZmonTvCookie(String token, HttpServletRequest request, HttpServletResponse response) {
        String cookieValue = Base64Utils.encodeToString(token.getBytes());
        Cookie cookie = WebUtils.getCookie(request, TvTokenService.ZMON_TV);
        if (cookie == null) {
            cookie = new Cookie(TvTokenService.ZMON_TV, cookieValue);
        } else {
            cookie.setValue(cookieValue);
        }
        cookie.setComment("ZMON_TV enables access for Team monitors.");
        cookie.setMaxAge((int) DAYS.toSeconds(365));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private void bindZmonUidCookie(String bindRandom, HttpServletRequest request, HttpServletResponse response) {
        String cookieValue = Base64Utils.encodeToString(bindRandom.getBytes());
        Cookie cookie = WebUtils.getCookie(request, TvTokenService.ZMON_TV_ID);
        if (cookie == null) {
            cookie = new Cookie(TvTokenService.ZMON_TV_ID, cookieValue);
        } else {
            cookie.setValue(cookieValue);
        }
        cookie.setComment("ZMON_TV_ID enables access for Team monitors.");
        cookie.setMaxAge((int) DAYS.toSeconds(365));
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}

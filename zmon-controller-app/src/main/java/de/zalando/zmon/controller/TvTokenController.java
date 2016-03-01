package de.zalando.zmon.controller;

import static java.util.concurrent.TimeUnit.DAYS;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import de.zalando.zmon.persistence.OnetimeTokensSProcService;
import de.zalando.zmon.security.tvtoken.TvTokenService;

/**
 * 
 * @author jbellmann
 *
 */
@Controller
public class TvTokenController {

    private OnetimeTokensSProcService oneTimeTokenSProcService;

    private final Joiner cookieValueJoiner = Joiner.on("|").useForNull("UNKNOWN");
    private final Splitter cookieValueSplitter = Splitter.on("|").omitEmptyStrings();

    private final TvTokenService tvTokenService;

    @Autowired
    public TvTokenController(TvTokenService tvTokenService) {
        this.tvTokenService = tvTokenService;
    }

    @RequestMapping("/tv/{token}")
    public String handleToken(@PathVariable String token,
            @RequestHeader(name = "X-FORWARDED-FOR", required = false) String bindIp,
            @CookieValue(value = "JSESSIONID", required = false) String sessionId, HttpServletRequest request,
            HttpServletResponse response) {
        if (!token.isEmpty() && token.length() > 5) {
            // check-token
            if (bindIp == null) {
                bindIp = remoteIp(request);
            }
            if (isValidToken(token, bindIp, sessionId)) {
                String cookieValue = createCookieValue(token, bindIp, sessionId);
                Cookie tokenCookie = new Cookie("ZMON_TV", cookieValue);
                tokenCookie.setComment("ZMON_TV enables access for Team monitors.");
                tokenCookie.setMaxAge((int) DAYS.toSeconds(365));
                tokenCookie.setPath("/");
                response.addCookie(tokenCookie);
            }
        }
        return "redirect:/";
    }

    protected boolean isValidToken(String token, String bindIp, String sessionId) {
        return tvTokenService.isValidToken(token, bindIp, sessionId);
    }

    protected String createCookieValue(String token, String bindIp, String sessionId) {
        return tvTokenService.createCookieValue(token, bindIp, sessionId);
    }

    public static String remoteIp(HttpServletRequest request) {
        if (request != null) {
            return request.getRemoteAddr();
        }
        return "UNKNOWN";
    }

}

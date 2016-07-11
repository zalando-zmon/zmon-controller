package org.zalando.zmon.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zalando.zmon.domain.OnetimeTokenInfo;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.OneTimeTokenService;
import org.zalando.zmon.service.TokenRequestResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by jmussler on 26.02.16.
 */
@Controller
@RequestMapping("/api/v1/onetime-tokens")
public class OneTimeTokenApi {

    OneTimeTokenService tokenService;
    DefaultZMonPermissionService authService;

    @Autowired
    public OneTimeTokenApi(OneTimeTokenService tokenService, DefaultZMonPermissionService authService) {
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> getNewToken(HttpServletRequest request) throws ZMonException, IOException {

        String token = OneTimeTokenService.randomString(8);

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        TokenRequestResult result = tokenService.storeToken(authService.getUserName(), ipAddress, token, 365);

        switch(result) {
            case RATE_LIMIT_HIT:
                return new ResponseEntity<>("Rate limit hit: wait 15 seconds", HttpStatus.TOO_MANY_REQUESTS);
            case OK:
                return new ResponseEntity<>(token, HttpStatus.OK);
        }

        return new ResponseEntity<>("Token create failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    @RequestMapping(value="", method=RequestMethod.GET)
    public List<OnetimeTokenInfo> getTokensByUser() {
        return tokenService.getTokensByUser(authService.getUserName());
    }
}

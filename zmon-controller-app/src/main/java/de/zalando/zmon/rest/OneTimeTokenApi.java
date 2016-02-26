package de.zalando.zmon.rest;

import de.zalando.zmon.exception.ZMonException;
import de.zalando.zmon.persistence.OnetimeTokensSProcService;
import de.zalando.zmon.security.AuthorityService;
import de.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Created by jmussler on 26.02.16.
 */
@Controller
@RequestMapping("/api/v1/onetime-tokens")
public class OneTimeTokenApi {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    @Autowired
    OnetimeTokensSProcService dbService;

    @Autowired
    DefaultZMonPermissionService authService;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> getNewToken(HttpServletRequest request) throws ZMonException, IOException {
        String token = randomString(8);

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        dbService.createOnetimeToken(authService.getUserName(), ipAddress, token);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}

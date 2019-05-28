package org.zalando.zmon.security.grafanatoken;

import com.google.common.collect.Lists;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.zalando.zmon.security.authority.ZMONRoleToAuthority;
import org.zalando.zmon.security.authority.ZMonRole;
import org.zalando.zmon.security.jwt.JWTServiceProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

public class GrafanaTokenService {

    public static final String GRAFANA_HEADER = "X-Grafana-Token";

    private final Logger log = LoggerFactory.getLogger(GrafanaTokenService.class);

    private final JWSVerifier verifier;

    public GrafanaTokenService(JWTServiceProperties jwtServiceProperties) {
        try {
            verifier = new MACVerifier(jwtServiceProperties.getKairosdbSignKey());
        } catch (JOSEException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Authentication authenticateFromHeader(HttpServletRequest request, HttpServletResponse response) {
        String jwtString = request.getHeader(GRAFANA_HEADER);
        if (jwtString == null) {
            return null;
        } else {
            if (!signedByApp(jwtString)) {
                // JWT was not signed with our current secret
                return null;
            } else {
                JWTClaimsSet claims = Try.of(() -> JWTParser.parse(jwtString).getJWTClaimsSet())
                        .getOrElse((JWTClaimsSet) null);
                if (claims != null) {
                    if (claims.getExpirationTime().getTime() < System.currentTimeMillis()) {
                        // JWT expired, treat same as unauthenticated
                        return null;
                    }
                    try {
                        String username = claims.getSubject();
                        GrantedAuthority authority = ZMONRoleToAuthority.createAutority(ZMonRole.KAIROS_READER.getRoleName(), username, Collections.emptySet());
                        return new RememberMeAuthenticationToken("GRAFANA_JWT",
                                new User(username, "N/A", Lists.newArrayList(authority)),
                                Lists.newArrayList(authority));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            return null;
        }
    }

    public boolean signedByApp(String jwtString) {
        return Try.of(() -> JWSObject.parse(jwtString).verify(verifier)).getOrElse(false);
    }
}

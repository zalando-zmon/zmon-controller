package org.zalando.zmon.security.jwt;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;
import org.zalando.zmon.security.authority.AbstractZMonAuthority;
import org.zalando.zmon.security.authority.ZMONRoleToAuthority;
import org.zalando.zmon.security.authority.ZMonAuthority;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import javaslang.control.Try;
import net.minidev.json.JSONStyle;

public class JWTService {

    private static final String ZMON = "ZMON";

    private static final String TEAMS_CLAIM = "teams";

    private static final String AUTHORITY_CLAIM = "authority";

    private static final String COOKIE_NAME = "ZMON_JWT";

    private final Logger log = LoggerFactory.getLogger(JWTService.class);

    private final JWSHeader header;
    private final JWSSigner signer;
    private final JWSVerifier verifier;
    private final JWSAlgorithm algorithm;

    public JWTService(JWTServiceProperties jwtServiceProperties) {
        try {
            this.signer = new MACSigner(jwtServiceProperties.getSecret());
            this.verifier = new MACVerifier(jwtServiceProperties.getSecret());
            this.algorithm = JWSAlgorithm.HS256;
            this.header = new JWSHeader.Builder(this.algorithm).build();
        } catch (KeyLengthException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (JOSEException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // this is cool, I don't care about exception-handling anymore
    public boolean signedByApp(String jwtString) {
        return Try.of(() -> JWSObject.parse(jwtString).verify(verifier)).getOrElse(false);
    }

    public Authentication authenticateFromCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        if (cookie == null) {
            return null;
        } else {
            String jwtString = cookie.getValue();
            if (!signedByApp(jwtString)) {
                return null;
            } else {
                JWTClaimsSet claims = Try.of(() -> JWTParser.parse(jwtString).getJWTClaimsSet())
                        .getOrElse((JWTClaimsSet) null);
                if (claims != null) {
                    if (claims.getExpirationTime().getTime() < System.currentTimeMillis()) {
                        log.info("JWT expired, unable to authenticate");
                        return null;
                    }
                    try {
                        String username = claims.getSubject();
                        Set<String> teams = StringUtils.commaDelimitedListToSet((String) claims.getClaim(TEAMS_CLAIM));
                        String rolename = (String) claims.getClaim(AUTHORITY_CLAIM);
                        GrantedAuthority authority = ZMONRoleToAuthority.createAutority(rolename, username, teams);
                        return new RememberMeAuthenticationToken("ZMON_JWT",
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

    public void removeCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        if (cookie != null) {
            cookie.setMaxAge(0);
            // also overwrite the value to make sure it does not contain a
            // JWT-Token anymore
            cookie.setValue("");
            response.addCookie(cookie);
        }
    }

    public void writeCookie(HttpServletRequest request, HttpServletResponse response,
                            Authentication successfulAuthentication) {
        Assert.notNull(successfulAuthentication, "'successfullAuthentication' should not be null");
        Assert.isInstanceOf(SocialAuthenticationToken.class, successfulAuthentication,
                "'successfullAuthentication' should be an instance of " + SocialAuthenticationToken.class.getName());
        try {
            String tokenValue = getJwtTokenValue(successfulAuthentication);
            Cookie cookie = new Cookie(COOKIE_NAME, tokenValue);
            cookie.setMaxAge(-1);// expire when browser closed
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);

            response.addCookie(cookie);
        } catch (JOSEException e) {
            throw new RuntimeException("Could not generate the JWT", e);
        }
    }

    //@formatter:off
    protected String getJwtTokenValue(Authentication authentication) throws JOSEException {
        final JWSObject signedJwt = sign(
                buildJWSObject(
                        buildClaimSet(authentication)));
        return signedJwt.serialize();
    }
    //@formatter:on

    protected JWSObject sign(JWSObject jwt) throws JOSEException {
        jwt.sign(signer);
        return jwt;
    }

    //@formatter:off
    protected JWTClaimsSet buildClaimSet(Authentication authentication) {
        final String username = ((SocialAuthenticationToken) authentication).getName();
        final String teams = extractTeamsFromAuthentication(authentication);
        final String authority = extractRoleFromAuthentication(authentication);
        log.debug("build claim for {}, with authority : {} and teams : {}", username, authority, teams);
        Assert.hasText(username, "'username' should never be null or empty");
        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .issuer(ZMON)
                .expirationTime(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
                .issueTime(new Date())
                .claim(TEAMS_CLAIM, teams)
                .claim(AUTHORITY_CLAIM, authority)
                .subject(username);

        return claimsBuilder.build();
    }
    //@formatter:on

    protected String extractRoleFromAuthentication(Authentication authentication) {
        GrantedAuthority authority = Iterables.getFirst(authentication.getAuthorities(), null);
        Assert.isInstanceOf(AbstractZMonAuthority.class, authority);
        return authority.getAuthority();
    }

    protected String extractTeamsFromAuthentication(Authentication authentication) {
        GrantedAuthority authority = Iterables.getFirst(authentication.getAuthorities(), null);
        Assert.isInstanceOf(AbstractZMonAuthority.class, authority);
        return StringUtils.collectionToCommaDelimitedString(((ZMonAuthority) authority).getTeams());
    }

    protected JWSObject buildJWSObject(JWTClaimsSet claims) {
        final String serializedJson = claims.toJSONObject().toJSONString(JSONStyle.LT_COMPRESS);
        return new JWSObject(header, new Payload(serializedJson));
    }
}

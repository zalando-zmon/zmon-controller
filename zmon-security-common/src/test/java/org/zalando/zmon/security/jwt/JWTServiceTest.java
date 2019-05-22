package org.zalando.zmon.security.jwt;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import com.google.common.collect.Lists;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;

import static org.assertj.core.api.Assertions.assertThat;

public class JWTServiceTest {

    private Connection<?> oauthConnection;

    private JWTService service;

    @Before
    public void setUp() {
        oauthConnection = Mockito.mock(Connection.class);
        ConnectionData data = Mockito.mock(ConnectionData.class);
        Mockito.when(data.getProviderId()).thenReturn("zauth");
        Mockito.when(oauthConnection.createData()).thenReturn(data);

        // default service for tests
        JWTServiceProperties props = new JWTServiceProperties();
        props.setSecret("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        props.setKairosdbSignKey("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        service = new JWTService(props);
    }

    @Test
    public void testJwtServicePropertiesNullSecret() {
        JWTServiceProperties props = new JWTServiceProperties();
        props.setKairosdbSignKey("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        // normally invoked by spring
        props.postConstruct();
        assertThat(props.getSecret()).hasSameSizeAs(UUID.randomUUID().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJwtServicePropertiesSecretToShort() {
        JWTServiceProperties props = new JWTServiceProperties();
        props.setSecret("012345678901234");
        // normally invoked by spring
        props.postConstruct();
    }

    @Test
    public void testJwtServicePropertiesSecret() {
        JWTServiceProperties props = new JWTServiceProperties();
        props.setSecret("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        props.setKairosdbSignKey("28PI9q068f2qCbT38hnGX279Wei5YU5n");
        // normally invoked by spring
        props.postConstruct();
    }

    @Test
    public void testJwtService() throws JOSEException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        service.writeCookie(request, response, getAuthentication());
        assertThat(response.getCookie("ZMON_JWT")).isNotNull();
        request.setCookies(response.getCookie("ZMON_JWT"));

    }

    protected Authentication getAuthentication() {
        GrantedAuthority ga = ZMonUserAuthority.FACTORY.create("kmeier", Collections.emptySet());
        SocialUserDetails userDetails = new SocialUser("kmeier", "geheim", Lists.newArrayList(ga));
        SocialAuthenticationToken t = new SocialAuthenticationToken(this.oauthConnection, userDetails,
                new HashMap<String, String>(), userDetails.getAuthorities());
        return t;
    }

    @Test
    public void testSignedJwt() throws JOSEException {
        String signedJwtString = service.getJwtTokenValue(getAuthentication());
        boolean isSignedByApp = service.signedByApp(signedJwtString);
        assertThat(isSignedByApp).isTrue();
    }

    @Test
    public void testUnsignedJwt() throws JOSEException {
        JWSSigner signer = new MACSigner("b6K0qetsB7SvRJq473rkfP6qEb56m4u9");
        JWSObject jwt = service.buildJWSObject(service.buildClaimSet(getAuthentication()));
        jwt.sign(signer);
        String jwtString = jwt.serialize();
        boolean isSignedByApp = service.signedByApp(jwtString);
        assertThat(isSignedByApp).isFalse();
    }
}

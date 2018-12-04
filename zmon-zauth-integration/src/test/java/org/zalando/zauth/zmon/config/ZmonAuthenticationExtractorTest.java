package org.zalando.zauth.zmon.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zauth.zmon.service.ZauthAuthorityService;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.authority.ZMonAuthority;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ZmonAuthenticationExtractorTest {

    private ZmonAuthenticationExtractor zmonAuthenticationExtractor;

    @Before
    public void setUp() {
        ZauthAuthorityService userService = mock(ZauthAuthorityService.class);
        doReturn(singletonList(new ZMonUserAuthority("klaus", ImmutableSet.of())))
                .when(userService).getAuthorities(eq("klaus"));
        zmonAuthenticationExtractor = new ZmonAuthenticationExtractor(userService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractorWithNullArgument() {
        zmonAuthenticationExtractor.createAuthorityList(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractorWithEmptyMap() {
        zmonAuthenticationExtractor.createAuthorityList(new HashMap<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractorWithEmptyUid() {
        Map<String, Object> tokenInfoResponse = new HashMap<>();
        tokenInfoResponse.put("uid", "");
        zmonAuthenticationExtractor.createAuthorityList(tokenInfoResponse);
    }

    @Test
    public void testExtractorUid() {
        Map<String, Object> tokenInfoResponse = new HashMap<>();
        tokenInfoResponse.put("uid", "klaus");
        List<GrantedAuthority> authorities = zmonAuthenticationExtractor.createAuthorityList(tokenInfoResponse);
        Assertions.assertThat(authorities).isNotEmpty();
        Assertions.assertThat(authorities.size()).isEqualTo(1);
        Assertions.assertThat(authorities.get(0)).isInstanceOf(ZMonUserAuthority.class);
        ZMonUserAuthority cast = (ZMonUserAuthority) authorities.get(0);
        Assertions.assertThat(cast.getTeams()).isEmpty();
    }

    @Test
    public void testExtractorUidAndTeams() {
    }

    @Test
    public void testExtractorUidAndNoTeams() {
    }

}

package org.zalando.zauth.zmon.config;

import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zauth.zmon.service.ZauthAuthorityService;
import org.zalando.zmon.security.DynamicTeamService;
import org.zalando.zmon.security.authority.ZMonAdminAuthority;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import java.util.*;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ZmonAuthenticationExtractorTest {

    private ZmonAuthenticationExtractor zmonAuthenticationExtractor;
    private DynamicTeamService dynamicTeamServiceMock;

    @Before
    public void setUp() {
        ZauthAuthorityService userService = mock(ZauthAuthorityService.class);
        doReturn(singletonList(new ZMonUserAuthority("klaus", ImmutableSet.of())))
                .when(userService).getAuthorities(eq("klaus"));

        dynamicTeamServiceMock = mock(DynamicTeamService.class);
        zmonAuthenticationExtractor = new ZmonAuthenticationExtractor(userService, dynamicTeamServiceMock);
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
        tokenInfoResponse.put("realm", "/employees");
        List<GrantedAuthority> authorities = zmonAuthenticationExtractor.createAuthorityList(tokenInfoResponse);
        Assertions.assertThat(authorities).isNotEmpty();
        Assertions.assertThat(authorities.size()).isEqualTo(1);
        Assertions.assertThat(authorities.get(0)).isInstanceOf(ZMonUserAuthority.class);
        ZMonUserAuthority cast = (ZMonUserAuthority) authorities.get(0);
        Assertions.assertThat(cast.getTeams()).isEmpty();
    }

    @Test
    public void testExtractorServiceAsUser() {
        doReturn(Optional.of(singletonList("SOME_TEAM"))).when(dynamicTeamServiceMock).getTeams(eq("robot"));
        Map<String, Object> tokenInfoResponse = new HashMap<>();
        tokenInfoResponse.put("uid", "robot");
        tokenInfoResponse.put("realm", "/services");
        List<GrantedAuthority> authorities = zmonAuthenticationExtractor.createAuthorityList(tokenInfoResponse);
        Assertions.assertThat(authorities).isNotEmpty();
        Assertions.assertThat(authorities.size()).isEqualTo(1);
        Assertions.assertThat(authorities.get(0)).isInstanceOf(ZMonUserAuthority.class);
        ZMonUserAuthority cast = (ZMonUserAuthority) authorities.get(0);
        Assertions.assertThat(cast.getTeams()).containsExactly("SOME_TEAM");
    }

    @Test
    public void testExtractorServiceAsAdmin() {
        doReturn(Optional.of(singletonList("ZMON"))).when(dynamicTeamServiceMock).getTeams(eq("robot"));
        Map<String, Object> tokenInfoResponse = new HashMap<>();
        tokenInfoResponse.put("uid", "robot");
        tokenInfoResponse.put("realm", "/services");
        List<GrantedAuthority> authorities = zmonAuthenticationExtractor.createAuthorityList(tokenInfoResponse);
        Assertions.assertThat(authorities).isNotEmpty();
        Assertions.assertThat(authorities.size()).isEqualTo(1);
        Assertions.assertThat(authorities.get(0)).isInstanceOf(ZMonAdminAuthority.class);
        ZMonAdminAuthority cast = (ZMonAdminAuthority) authorities.get(0);
        Assertions.assertThat(cast.getTeams()).containsExactly("ZMON");
    }

    @Test
    public void testExtractorUidAndTeams() {
    }

    @Test
    public void testExtractorUidAndNoTeams() {
    }

}

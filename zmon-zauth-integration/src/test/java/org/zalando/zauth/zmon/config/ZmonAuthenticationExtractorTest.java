package org.zalando.zauth.zmon.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.zalando.zauth.zmon.config.ZmonAuthenticationExtractor;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import com.google.common.collect.Sets;

public class ZmonAuthenticationExtractorTest {

    private TeamService teamService;
    private ZmonAuthenticationExtractor zmonAuthenticationExtractor;

    @Before
    public void setUp() {
        teamService = Mockito.mock(TeamService.class);
        zmonAuthenticationExtractor = new ZmonAuthenticationExtractor(teamService);
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
        assertAuthorityList(authorities);
        ZMonUserAuthority cast = (ZMonUserAuthority)authorities.get(0);
        Assertions.assertThat(cast.getTeams()).isEmpty();
    }

    @Test
    public void testExtractorUidAndTeams() {
        Mockito.when(teamService.getTeams(Mockito.eq("klaus"))).thenReturn(Sets.newHashSet("one", "two"));
        Map<String, Object> tokenInfoResponse = new HashMap<>();
        tokenInfoResponse.put("uid", "klaus");
        tokenInfoResponse.put("realm", "/employees");
        List<GrantedAuthority> authorities = zmonAuthenticationExtractor.createAuthorityList(tokenInfoResponse);
        assertAuthorityList(authorities);
        ZMonUserAuthority cast = (ZMonUserAuthority) authorities.get(0);
        Assertions.assertThat(cast.getTeams()).contains("one", "two");
    }

    @Test
    public void testExtractorUidAndNoTeams() {
        Mockito.when(teamService.getTeams(Mockito.eq("klaus"))).thenReturn(Sets.newHashSet("one", "two"));
        Map<String, Object> tokenInfoResponse = new HashMap<>();
        tokenInfoResponse.put("uid", "klaus");
        List<GrantedAuthority> authorities = zmonAuthenticationExtractor.createAuthorityList(tokenInfoResponse);
        assertAuthorityList(authorities);
        ZMonUserAuthority cast = (ZMonUserAuthority) authorities.get(0);
        Assertions.assertThat(cast.getTeams()).isEmpty();
    }

    protected void assertAuthorityList(List<GrantedAuthority> authorities) {
        Assertions.assertThat(authorities).isNotEmpty();
        Assertions.assertThat(authorities.size()).isEqualTo(1);
        Assertions.assertThat(authorities.get(0)).isInstanceOf(ZMonUserAuthority.class);
    }

}

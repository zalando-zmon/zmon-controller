package org.zalando.zauth.zmon.service;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zauth.zmon.config.ZauthProperties;
import org.zalando.zmon.security.DynamicTeamService;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.authority.ZMonAdminAuthority;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ZauthAuthorityServiceTest {
    private ZauthAuthorityService service;

    @Before
    public void setUp() throws Exception {
        final ZauthProperties zauthProperties = new ZauthProperties();
        zauthProperties.setUserServiceUrl(new URL("http://user-service.com"));

        final TeamService teamServiceMock = mock(TeamService.class);
        doReturn(ImmutableSet.of("test-team")).when(teamServiceMock).getTeams("test-user");
        doReturn(ImmutableSet.of("test-team")).when(teamServiceMock).getTeams("test-admin");

        final DynamicTeamService dynamicTeamServiceMock = mock(DynamicTeamService.class);
        doReturn(Optional.of(Collections.singletonList("test-team"))).when(dynamicTeamServiceMock).getTeams("test-service");

        service = new ZauthAuthorityService(zauthProperties, teamServiceMock, dynamicTeamServiceMock, mock(AccessTokens.class)) {
            @Override
            protected Set<String> getGroups(String username) {
                return "test-admin".equals(username) ?
                        ImmutableSet.of("Apps/ZMON/Admins") :
                        ImmutableSet.of("Apps/ZMON/Users");
            }
        };
    }

    @Test
    public void getAuthorities_user() {
        final Collection<? extends GrantedAuthority> authorities = service.getAuthorities("test-user");
        assertEquals(1, authorities.size());
        assertEquals(ZMonUserAuthority.class, authorities.iterator().next().getClass());
    }

    @Test
    public void getAuthorities_admin() {
        final Collection<? extends GrantedAuthority> authorities = service.getAuthorities("test-admin");
        assertEquals(1, authorities.size());
        assertEquals(ZMonAdminAuthority.class, authorities.iterator().next().getClass());
    }

    @Test
    public void getAuthorities_service() {
        final Collection<? extends GrantedAuthority> authorities = service.getAuthorities("test-service");
        assertEquals(1, authorities.size());
        assertEquals(ZMonUserAuthority.class, authorities.iterator().next().getClass());
    }
}
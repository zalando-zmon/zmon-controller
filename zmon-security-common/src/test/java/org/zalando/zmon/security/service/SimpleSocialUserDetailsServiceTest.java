package org.zalando.zmon.security.service;

import static java.util.Collections.singleton;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.zalando.zmon.security.authority.ZMonUserAuthority.FACTORY;

import java.util.Collection;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.security.SocialUserDetails;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.authority.AbstractZMonAuthority;

import com.google.common.collect.Sets;

public class SimpleSocialUserDetailsServiceTest {

    private UserDetailsService userDetailsService;
    private TeamService teamService;

    private SimpleSocialUserDetailsService service;

    private static final Set<String> FIRST_RESULT = Sets.newHashSet("TEAM_A", "TEAM_B");
    private static final Set<String> SECOND_RESULT = Sets.newHashSet("TEAM_A", "TEAM_B", "TEAM_C");

    @Before
    public void setUp() {
        userDetailsService = mock(UserDetailsService.class);
        teamService = mock(TeamService.class);
        service = new SimpleSocialUserDetailsService(userDetailsService, teamService);

        when(userDetailsService.loadUserByUsername(Mockito.eq("kmeier"))).thenReturn(createUserDetails());

        when(teamService.getTeams(Mockito.eq("kmeier"))).thenReturn(FIRST_RESULT, SECOND_RESULT);
    }

    @Test
    public void testTeamReload() {
        SocialUserDetails socialUserDetails = service.loadUserByUserId("kmeier");
        final Set<String> teams = getTeams(socialUserDetails.getAuthorities());
        Assertions.assertThat(teams).containsAll(FIRST_RESULT);
        //
        socialUserDetails = service.loadUserByUserId("kmeier");
        final Set<String> teams_2 = getTeams(socialUserDetails.getAuthorities());
        Assertions.assertThat(teams_2).containsAll(SECOND_RESULT);

    }

    private Set<String> getTeams(Collection<? extends GrantedAuthority> authorities) {
        GrantedAuthority authority = service.getFirstAuthority(authorities);
        return ((AbstractZMonAuthority) authority).getTeams();
    }

    private User createUserDetails() {
        return new User("kmeier", "NO", singleton(FACTORY.create("kmeier", Sets.newHashSet("TEAM_A", "TEAM_B"))));
    }
}

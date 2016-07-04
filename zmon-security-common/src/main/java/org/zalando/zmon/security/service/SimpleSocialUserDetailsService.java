package org.zalando.zmon.security.service;

import static org.zalando.zmon.security.authority.ZMONRoleToAuthority.createAutority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.util.Assert;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.authority.AbstractZMonAuthority;

import com.google.common.collect.Iterables;

public class SimpleSocialUserDetailsService implements SocialUserDetailsService {

    private final Logger log = LoggerFactory.getLogger(SimpleSocialUserDetailsService.class);

    private final UserDetailsService userDetailsService;
    private final TeamService teamService;

    public SimpleSocialUserDetailsService(final UserDetailsService userDetailsService, TeamService teamService) {
        this.userDetailsService = userDetailsService;
        this.teamService = teamService;
    }

    @Override
    public SocialUserDetails loadUserByUserId(final String username)
            throws UsernameNotFoundException, DataAccessException {

        try {
            log.info("Loading user {}..", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            GrantedAuthority authority = reloadTeams(userDetails);
            return new SocialUser(userDetails.getUsername(), "password", Collections.singleton(authority));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected GrantedAuthority reloadTeams(UserDetails userDetails) {
        String role = getRole(userDetails);
        return createAutority(role, userDetails.getUsername(), getTeams(userDetails.getUsername()));
    }

    protected String getRole(UserDetails userDetails) {
        GrantedAuthority authority = getFirstAuthority(userDetails.getAuthorities());
        return ((AbstractZMonAuthority) authority).getAuthority();
    }

    protected GrantedAuthority getFirstAuthority(Collection<? extends GrantedAuthority> authorities) {
        GrantedAuthority authority = Iterables.getFirst(authorities, null);
        Assert.notNull(authority, "Expect one object of 'GrantedAuthority' here");
        return authority;
    }

    protected Set<String> getTeams(String username) {
        return teamService.getTeams(username);
    }
}

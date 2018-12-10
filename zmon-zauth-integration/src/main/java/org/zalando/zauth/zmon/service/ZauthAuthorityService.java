package org.zalando.zauth.zmon.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.opentracing.contrib.spring.web.client.TracingRestTemplateInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.stups.oauth2.spring.client.StupsOAuth2RestTemplate;
import org.zalando.stups.oauth2.spring.client.StupsTokensAccessTokenProvider;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zauth.zmon.config.ZauthProperties;
import org.zalando.zauth.zmon.domain.Group;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.DynamicTeamService;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.authority.ZMonAdminAuthority;
import org.zalando.zmon.security.authority.ZMonAuthority;
import org.zalando.zmon.security.authority.ZMonUserAuthority;

import java.util.*;
import java.util.stream.Collectors;

public class ZauthAuthorityService implements AuthorityService {

    private final Logger log = LoggerFactory.getLogger(ZauthAuthorityService.class);

    private final ZauthProperties zauthProperties;
    private final TeamService teamService;
    private final DynamicTeamService dynamicTeamService;
    private final RestTemplate restTemplate;

    public ZauthAuthorityService(ZauthProperties zauthProperties,
                                 TeamService teamService,
                                 DynamicTeamService dynamicTeamService,
                                 AccessTokens accessTokens) {
        Preconditions.checkNotNull(zauthProperties.getUserServiceUrl(), "User Service URL must be set");

        this.zauthProperties = zauthProperties;
        this.teamService = teamService;
        this.dynamicTeamService = dynamicTeamService;

        restTemplate = new StupsOAuth2RestTemplate(new StupsTokensAccessTokenProvider("user-service", accessTokens));
        restTemplate.getInterceptors().add(new TracingRestTemplateInterceptor());
    }

    protected Set<String> getGroups(String username) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(zauthProperties.getUserServiceUrl().toString())
                .path("/api/employees/" + username + "/groups");

        try {
            Group[] groups = restTemplate.getForObject(builder.build().toUri(), Group[].class);
            return Arrays.stream(groups).map(Group::getName).collect(Collectors.toSet());
        } catch (Exception e) {
            return ImmutableSet.of();
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(String username) {
        Set<String> groups = getGroups(username);

        ZMonAuthority authority;
        if (groups.contains(zauthProperties.getAdminsGroup())) {
            authority = new ZMonAdminAuthority(username, ImmutableSet.copyOf(teamService.getTeams(username)));
        } else if (groups.contains(zauthProperties.getUsersGroup())) {
            authority = new ZMonUserAuthority(username, ImmutableSet.copyOf(teamService.getTeams(username)));
        } else {
            final List<String> teams = dynamicTeamService.getTeams(username).orElse(Collections.emptyList());
            authority = new ZMonUserAuthority(username, ImmutableSet.copyOf(teams));
        }

        log.info("User {} has authority {} and teams {}", username, authority.getAuthority(), authority.getTeams());

        return Lists.newArrayList(authority);
    }
}

package org.zalando.zauth.zmon.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.stups.oauth2.spring.client.StupsOAuth2RestTemplate;
import org.zalando.stups.oauth2.spring.client.StupsTokensAccessTokenProvider;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zauth.zmon.config.ZauthProperties;
import org.zalando.zauth.zmon.domain.Team;
import org.zalando.zmon.security.TeamService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by hjacobs on 2/4/16.
 */
public class ZauthTeamService implements TeamService {

    private final Logger log = LoggerFactory.getLogger(ZauthTeamService.class);

    private static final ParameterizedTypeReference<List<Team>> TYPE_REF = new ParameterizedTypeReference<List<Team>>() {
    };

    private ZauthProperties zauthProperties;
    private RestTemplate restTemplate;

    public ZauthTeamService(ZauthProperties zauthProperties, AccessTokens accessTokens) {
        Preconditions.checkNotNull(zauthProperties.getTeamServiceUrl(), "Team Service URL must be set");
        Preconditions.checkNotNull(accessTokens, "accessTokens cannot be null");

        this.zauthProperties = zauthProperties;

        restTemplate = new StupsOAuth2RestTemplate(new StupsTokensAccessTokenProvider("team-service", accessTokens));
        log.info("Configured Team Service with URL {}", zauthProperties.getTeamServiceUrl());
        log.info("Overlay configured: {}", zauthProperties.getTeamOverlay().entrySet());
    }

    @Override
    public Set<String> getTeams(String username) {
        Set<String> result = Sets.newHashSet();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(zauthProperties.getTeamServiceUrl().toString()).path("/api/teams")
                .queryParam("member", username);

        try {
            List<Team> teams = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null, TYPE_REF).getBody();
            for (Team team : teams) {
                result.add(team.getName());
            }
        }
        catch(RestClientException ex) {
            log.error("Failed to call team service, no teams for now!", ex);
        }

        if(zauthProperties.getTeamOverlay().containsKey(username)) {
            List<String> teams = zauthProperties.getTeamOverlay().get(username);
            log.info("Adding teams from overlay to {}: {}", username, teams);
            result.addAll(teams);
        }

        Set<String> addByExtension = new TreeSet<>();
        for(String k : result) {
            if (zauthProperties.getTeamExtension().containsKey(k)) {
                List<String> toAdd = zauthProperties.getTeamExtension().get(k);
                addByExtension.addAll(toAdd);
            }
        }
        result.addAll(addByExtension);

        return result;
    }
}

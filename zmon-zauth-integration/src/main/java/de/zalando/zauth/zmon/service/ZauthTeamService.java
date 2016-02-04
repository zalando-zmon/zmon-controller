package de.zalando.zauth.zmon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.zalando.zauth.zmon.config.ZauthProperties;
import de.zalando.zauth.zmon.domain.Team;
import de.zalando.zmon.security.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.stups.oauth2.spring.client.StupsOAuth2RestTemplate;
import org.zalando.stups.oauth2.spring.client.StupsTokensAccessTokenProvider;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.ZmonOAuth2Properties;

import java.util.List;
import java.util.Set;

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
    }

    @Override
    public Set<String> getTeams(String username) {
        Set<String> result = Sets.newHashSet();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(zauthProperties.getTeamServiceUrl().toString()).path("/api/teams")
                .queryParam("member", username);

        List<Team> teams = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, null, TYPE_REF).getBody();
        for (Team team : teams) {
            result.add(team.getName());
        }
        return result;
    }
}

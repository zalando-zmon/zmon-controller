package org.zalando.zmon.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.zalando.stups.oauth2.spring.server.TokenInfoRequestExecutor;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zauth.zmon.config.ZauthProperties;
import org.zalando.zauth.zmon.config.ZmonAuthenticationExtractor;
import org.zalando.zauth.zmon.service.ZauthAuthorityService;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.DynamicTeamService;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.ZmonResourceServerConfigurer;
import org.zalando.zmon.security.service.ChainedResourceServerTokenServices;
import org.zalando.zmon.security.service.PresharedTokensResourceServerTokenServices;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Configuration
@PropertySource("classpath:/test.properties")
@EnableWebSecurity
@EnableResourceServer
@EnableConfigurationProperties({ZauthProperties.class})
@Order(4)
public class TestSecurityConfig {

    @Qualifier("mockAccessTokensBean")
    @Autowired
    AccessTokens accessTokens;

    @Autowired
    Environment environment;

    @Bean
    public ResourceServerConfigurer testResourceServerConfigurer(ZauthProperties zauthProperties) throws MalformedURLException {
        zauthProperties.setUserServiceUrl(new URL("http://user-service.com"));

        final TeamService teamServiceMock = mock(TeamService.class);
        doReturn(ImmutableSet.of("test-team")).when(teamServiceMock).getTeams("test-employee");

        final DynamicTeamService dynamicTeamServiceMock = mock(DynamicTeamService.class);
        doReturn(Optional.of(Collections.singletonList("test-team"))).when(dynamicTeamServiceMock).getTeams("test-service");

        final AuthorityService authorityService = new ZauthAuthorityService(zauthProperties, teamServiceMock, dynamicTeamServiceMock, accessTokens) {
            @Override
            protected Set<String> getGroups(String username) {
                return "test-employee".equals(username) ? ImmutableSet.of("Apps/ZMON/Users") : Collections.emptySet();
            }
        };

        final ZmonAuthenticationExtractor extractor = new ZmonAuthenticationExtractor(authorityService);

        final TokenInfoRequestExecutor requestExecutorMock = mock(TokenInfoRequestExecutor.class);
        doReturn(ImmutableMap.of("uid", "test-employee", "realm", "/employees"))
                .when(requestExecutorMock).getMap(eq("test-employee-token"));
        doReturn(ImmutableMap.of("uid", "test-service", "realm", "/services"))
                .when(requestExecutorMock).getMap(eq("test-service-token"));

        return new ZmonResourceServerConfigurer(new ChainedResourceServerTokenServices(ImmutableList.of(
                new PresharedTokensResourceServerTokenServices(authorityService, environment),
                new TokenInfoResourceServerTokenServices("CLIENT_ID_NOT_NEEDED", extractor, requestExecutorMock)
        )));

    }
}

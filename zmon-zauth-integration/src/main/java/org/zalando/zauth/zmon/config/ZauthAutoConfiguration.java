package org.zalando.zauth.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zauth.zmon.service.ZauthAuthorityService;
import org.zalando.zauth.zmon.service.ZauthTeamService;
import org.zalando.zmon.config.ZmonOAuth2Properties;
import org.zalando.zmon.persistence.EntitySProcService;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.SigninController;
import org.zalando.zmon.security.TeamService;

/**
 * @author jbellmann
 */
@Configuration
@EnableConfigurationProperties({ZmonOAuth2Properties.class, ZauthProperties.class})
@Import({ZauthSecurityConfig.class, ZauthSocialConfigurer.class})
@Profile("zauth")
public class ZauthAutoConfiguration {

    @Autowired
    private ZauthProperties zauthProperties;

    @Autowired
    private EntitySProcService entitySProcService;

    @Bean
    public SigninController signinController() {
        return new SigninController();
    }

    @Bean
    public TeamService teamService(AccessTokens accessTokens) {
        return new ZauthTeamService(zauthProperties, accessTokens, entitySProcService);
    }

    @Bean
    public AuthorityService authorityService(TeamService teamService, AccessTokens accessTokens) {
        return new ZauthAuthorityService(zauthProperties, teamService, accessTokens);
    }
}

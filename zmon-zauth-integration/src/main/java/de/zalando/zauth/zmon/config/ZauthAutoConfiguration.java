package de.zalando.zauth.zmon.config;

import de.zalando.zauth.zmon.service.ZauthTeamService;
import de.zalando.zmon.security.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import org.zalando.stups.tokens.AccessToken;
import org.zalando.stups.tokens.AccessTokenUnavailableException;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.stups.tokens.Tokens;
import org.zalando.zmon.config.ZmonOAuth2Properties;
import org.zalando.zmon.security.SigninController;

import java.net.URISyntaxException;

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

    @Bean
    public SigninController signinController() {
        return new SigninController();
    }

    @Bean
    public TeamService teamService(AccessTokens accessTokens) {
        return new ZauthTeamService(zauthProperties, accessTokens);
    }

    @Bean
    public AccessTokens accessTokens() throws URISyntaxException {
        return Tokens.createAccessTokensWithUri(zauthProperties.getOauth2AccessTokenUrl().toURI()).manageToken("team-service").addScope("uid").done().start();
    }
}

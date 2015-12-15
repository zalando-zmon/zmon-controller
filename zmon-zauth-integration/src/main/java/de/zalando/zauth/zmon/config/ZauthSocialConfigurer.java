package de.zalando.zauth.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.zauth.config.AbstractZAuthSocialConfigurer;
import org.zalando.zmon.config.ZmonOAuth2Properties;

import de.zalando.zauth.zmon.service.ZauthAccountConnectionSignupService;
import de.zalando.zmon.security.AuthorityService;

import org.zalando.stups.tokens.JsonFileBackedClientCredentialsProvider;
import org.zalando.stups.tokens.ClientCredentialsProvider;

/**
 * @author jbellmann
 */
@Configuration
@EnableSocial
public class ZauthSocialConfigurer extends AbstractZAuthSocialConfigurer {

    @Autowired
    private ZmonOAuth2Properties zmonOAuth2Properties;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private AuthorityService authorityService;

    @Override
    protected UsersConnectionRepository doGetUsersConnectionRepository(
            final ConnectionFactoryLocator connectionFactoryLocator) {

        // for the example 'InMemory' is ok, but could be also JDBC or custom
        InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
        repository.setConnectionSignUp(new ZauthAccountConnectionSignupService(userDetailsManager, authorityService));
        return repository;
    }

    protected ClientCredentialsProvider getClientCredentialsProvider() {
        return new JsonFileBackedClientCredentialsProvider();
    }

    @Override
    protected String getClientId() {

        String clientId = zmonOAuth2Properties.getClientId();
        if (clientId == null) {
            return getClientCredentialsProvider().get().getId();
        } else {
            return clientId;
        }
    }

    @Override
    protected String getClientSecret() {
        String clientSecret = zmonOAuth2Properties.getClientSecret();
        if (clientSecret == null) {
            return getClientCredentialsProvider().get().getSecret();
        } else {
            return clientSecret;
        }
    }
}

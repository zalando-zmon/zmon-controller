package org.zalando.zauth.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.oauth2.ClientCredentialsSupplier;
import org.springframework.social.oauth2.JsonCredentialFileReader;
import org.springframework.social.oauth2.PlatformCredentialsetFileReader;
import org.springframework.social.zauth.config.AbstractZAuthSocialConfigurer;
import org.zalando.zauth.zmon.service.ZauthAccountConnectionSignupService;
import org.zalando.zmon.config.ZmonOAuth2Properties;
import org.zalando.zmon.security.AuthorityService;

/**
 * @author jbellmann
 */
@Configuration
@EnableSocial
public class ZauthSocialConfigurer extends AbstractZAuthSocialConfigurer {
    private static final String AUTHORIZE_URL = "https://auth.zalando.com/oauth2/authorize";
    private static final String ACCESS_TOKEN_URL = "https://auth.zalando.com/oauth2/access_token?realm=/employees";

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

    @Override
    protected ClientCredentialsSupplier getClientCredentialsSupplier() {

        if (zmonOAuth2Properties.isPlatformEnabled()) {
            return new PlatformCredentialsetFileReader(
                    zmonOAuth2Properties.getCredentialsDirectoryPath(),
                    zmonOAuth2Properties.getPlatformTokenName()
            );
        }

        return new JsonCredentialFileReader(
                zmonOAuth2Properties.getCredentialsDirectoryPath()
        );
    }


    @Override
    protected String getAuthorizationEndpoint() {
        return zmonOAuth2Properties.getAuthorizeUrl();
    }

    @Override
    protected String getTokenEndpoint() {
        return zmonOAuth2Properties.getAccessTokenUrl();
    }
}

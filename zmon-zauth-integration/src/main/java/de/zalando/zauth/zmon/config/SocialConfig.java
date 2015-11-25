package de.zalando.zauth.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.zauth.config.AbstractZAuthSocialConfigurer;

import de.zalando.zauth.zmon.service.AccountConnectionSignupService;

@Configuration
@EnableSocial
@EnableConfigurationProperties({ ZAuthProperties.class })
public class SocialConfig extends AbstractZAuthSocialConfigurer {

    @Autowired
    private ZAuthProperties zauthProperties;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Override
    protected UsersConnectionRepository doGetUsersConnectionRepository(
            final ConnectionFactoryLocator connectionFactoryLocator) {

        // for the example 'InMemory' is ok, but could be also JDBC or custom
        InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
        repository.setConnectionSignUp(new AccountConnectionSignupService(userDetailsManager));
        return repository;
    }

    @Override
    protected String getClientId() {

        return zauthProperties.getClientId();
    }

    @Override
    protected String getClientSecret() {

        return zauthProperties.getClientSecret();
    }
}

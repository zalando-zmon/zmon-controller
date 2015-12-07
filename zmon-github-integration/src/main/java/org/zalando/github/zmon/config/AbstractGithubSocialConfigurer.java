package org.zalando.github.zmon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.github.security.GitHubAuthenticationService;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;

/**
 * 
 * @author jbellmann
 *
 */
public abstract class AbstractGithubSocialConfigurer implements SocialConfigurer {

    private final Logger log = LoggerFactory.getLogger(AbstractGithubSocialConfigurer.class);

    @Override
    public void addConnectionFactories(final ConnectionFactoryConfigurer connectionFactoryConfigurer,
            final Environment environment) {
        // we do not add the 'connectionFactory' here
        // because of registering it in #getUsersConnectionRepository below
    }

    @Override
    public UserIdSource getUserIdSource() {

        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(
            final ConnectionFactoryLocator connectionFactoryLocator) {

        // this is hacky, but didn't found out how to do these configuration without it
        if (connectionFactoryLocator instanceof SocialAuthenticationServiceRegistry) {
        	
        	if(log.isDebugEnabled()){        		
        		log.debug("Initialize ConnectionFactory with key {} and secret {}",
        				getClientId().substring(0, getClientIdSubstringLenght()),
        				getClientSecret().substring(0, getClientSecretIdSubstringCount()));
        	}

            SocialAuthenticationServiceRegistry registry = (SocialAuthenticationServiceRegistry)
                connectionFactoryLocator;
            registry.addAuthenticationService(new GitHubAuthenticationService(getClientId(), getClientSecret()));
        }

        return doGetUsersConnectionRepository(connectionFactoryLocator);
    }

    protected abstract UsersConnectionRepository doGetUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator);

    protected abstract String getClientId();

    protected abstract String getClientSecret();

    protected int getClientIdSubstringLenght() {
        return 8;
    }

    protected int getClientSecretIdSubstringCount() {
        return 4;
    }
}

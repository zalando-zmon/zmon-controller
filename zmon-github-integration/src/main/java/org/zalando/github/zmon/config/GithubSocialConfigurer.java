package org.zalando.github.zmon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.github.security.GitHubAuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.zalando.zmon.config.ZmonOAuth2Properties;
import org.zalando.zmon.security.AbstractZmonSocialConfigurer;
import org.zalando.zmon.security.service.AccountConnectionSignupService;

import de.zalando.zmon.security.AuthorityService;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@EnableSocial
public class GithubSocialConfigurer extends AbstractZmonSocialConfigurer {

    private final Logger log = LoggerFactory.getLogger(GithubSocialConfigurer.class);
	
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

		//
		repository.setConnectionSignUp(new AccountConnectionSignupService(userDetailsManager, authorityService));
		return repository;
	}

	@Override
	protected String getClientId() {
		return zmonOAuth2Properties.getClientId();
	}

	@Override
	protected String getClientSecret() {
		return zmonOAuth2Properties.getClientSecret();
	}

	@Override
	protected SocialAuthenticationService<?> buildAuthenticationService() {
		return new GitHubAuthenticationService(getClientId(), getClientSecret());
	}

}

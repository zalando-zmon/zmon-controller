package de.zalando.zauth.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.zauth.config.AbstractZAuthSocialConfigurer;
import org.zalando.zmon.config.ZmonOAuth2Properties;

import de.zalando.zauth.zmon.service.ZauthAccountConnectionSignupService;
import de.zalando.zmon.security.AuthorityService;

/**
 * 
 * @author jbellmann
 *
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

	@Override
	protected String getClientId() {

		return zmonOAuth2Properties.getClientId();
	}

	@Override
	protected String getClientSecret() {

		return zmonOAuth2Properties.getClientSecret();
	}
}

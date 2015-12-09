package org.zalando.github.zmon.service;

import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.zalando.github.zmon.security.GithubSignupCondition;
import org.zalando.zmon.security.service.AccountConnectionSignupService;

import de.zalando.zmon.security.AuthorityService;

/**
 * 
 * @author jbellmann
 *
 */
public class GithubAccountConnectionSignupService extends AccountConnectionSignupService {
	
	private GithubSignupCondition signupCondition;

	public GithubAccountConnectionSignupService(UserDetailsManager userDetailsManager, AuthorityService authorityService, GithubSignupCondition signupCondition) {
		super(userDetailsManager, authorityService);
		this.signupCondition = signupCondition;
	}

	@Override
	protected boolean passesSignupConditions(Connection<?> connection) {
		return this.signupCondition.apply((GitHub)connection.getApi());
	}



	@Override
	protected String getLoginFromConnection(Connection<?> connection) {
		Object api = connection.getApi();
		// use the api if you can
		if (api instanceof GitHub) {
			GitHub github = (GitHub) api;
			return github.userOperations().getProfileId();
		}

		return "not_found";
	}

}

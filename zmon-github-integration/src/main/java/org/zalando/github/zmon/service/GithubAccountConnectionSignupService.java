package org.zalando.github.zmon.service;

import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.zalando.zmon.security.service.AccountConnectionSignupService;

/**
 * 
 * @author jbellmann
 *
 */
public class GithubAccountConnectionSignupService extends AccountConnectionSignupService {

	public GithubAccountConnectionSignupService(UserDetailsManager userDetailsManager) {
		super(userDetailsManager);
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

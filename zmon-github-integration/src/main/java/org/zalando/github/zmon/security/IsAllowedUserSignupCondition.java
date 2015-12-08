package org.zalando.github.zmon.security;

import java.util.List;

import org.springframework.social.github.api.GitHub;

/**
 * 
 * @author jbellmann
 *
 */
public class IsAllowedUserSignupCondition extends GithubSignupCondition {

	private final List<String> allowedUsers;

	public IsAllowedUserSignupCondition(List<String> allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	@Override
	public boolean matches(GitHub api) {
		if (allowedUsers.isEmpty()) {
			return true;
		}
		return allowedUsers.contains(api.userOperations().getProfileId());
	}
}

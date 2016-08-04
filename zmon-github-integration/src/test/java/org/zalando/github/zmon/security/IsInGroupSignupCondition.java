package org.zalando.github.zmon.security;

import java.util.List;

import org.springframework.social.github.api.GitHub;

/**
 * 
 * @author jbellmann
 *
 */
public class IsInGroupSignupCondition extends GithubSignupCondition {

	private final List<String> allowedGroups;

	public IsInGroupSignupCondition(List<String> allowedGroups) {
		this.allowedGroups = allowedGroups;
	}

	@Override
	public boolean matches(GitHub api) {
		if (allowedGroups.isEmpty()) {
			return true;
		}
		return false;
	}

}

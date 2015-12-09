package org.zalando.github.zmon.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.github.api.GitHub;
import org.springframework.util.Assert;

/**
 * 
 * @author jbellmann
 *
 */
public class IsAllowedUserSignupCondition extends GithubSignupCondition {
	
	private final Logger log = LoggerFactory.getLogger(IsAllowedUserSignupCondition.class);

	private final GithubSignupConditionProperties signupProperties;

	public IsAllowedUserSignupCondition(GithubSignupConditionProperties signupProperties) {
		Assert.notNull(signupProperties, "'signupProperties' should never be null");
		this.signupProperties = signupProperties;
	}

	@Override
	public boolean matches(GitHub api) {
		log.info("CHECK FOR USER ...");
		if (signupProperties.getAllowedUsers().isEmpty()) {
			return signupProperties.isAlwaysAllowWhenUsersEmtpy() ? true : false;
		}
		String username = api.userOperations().getProfileId();
		log.info("USER : {}", username);
		return signupProperties.getAllowedUsers().contains(username);
	}
}

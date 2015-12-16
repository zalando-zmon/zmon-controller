package org.zalando.github.zmon.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.social.github.api.GitHub;

import org.springframework.util.Assert;

/**
 * @author  jbellmann
 */
public class IsAllowedUserSignupCondition extends GithubSignupCondition {

    private final Logger log = LoggerFactory.getLogger(IsAllowedUserSignupCondition.class);

    private final GithubSignupConditionProperties signupProperties;

    public IsAllowedUserSignupCondition(final GithubSignupConditionProperties signupProperties) {
        Assert.notNull(signupProperties, "'signupProperties' should never be null");
        this.signupProperties = signupProperties;
    }

    @Override
    public boolean matches(final GitHub api) {
        log.info("Check for user ...");

        if (signupProperties.getAllowedUsers().isEmpty()) {
            return false;
        }

        if (signupProperties.getAllowedUsers().contains(ALL_AUTHORIZED)) {
            return true;
        }

        final String username = api.userOperations().getProfileId();
        log.info("User : {}", username);

        return signupProperties.getAllowedUsers().contains(username);
    }
}

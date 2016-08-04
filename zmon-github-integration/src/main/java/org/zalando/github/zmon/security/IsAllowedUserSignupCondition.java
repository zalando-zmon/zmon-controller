package org.zalando.github.zmon.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.github.api.GitHub;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jbellmann
 */
public class IsAllowedUserSignupCondition extends GithubSignupCondition {

    private final Logger log = LoggerFactory.getLogger(IsAllowedUserSignupCondition.class);

    private final List<String> allowedUsers;

    public IsAllowedUserSignupCondition(final GithubSignupConditionProperties signupProperties) {
        Assert.notNull(signupProperties, "'signupProperties' should never be null");
        allowedUsers = signupProperties.getAllowedUsers().stream().map(String::toLowerCase).collect(Collectors.toList());
        logAllowedUsers();
    }

    @Override
    public boolean matches(final GitHub api) {
        if (allowedUsers.isEmpty()) {
            return false;
        }

        if (allowedUsers.contains(ALL_AUTHORIZED)) {
            return true;
        }

        final String username = api.userOperations().getProfileId();

        final boolean isAllowedUser = allowedUsers.contains(username.toLowerCase());

        return isAllowedUser;
    }

    protected void logAllowedUsers() {
        log.info("Github users allowed: {}", allowedUsers);
    }
}

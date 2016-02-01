package org.zalando.github.zmon.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.social.github.api.GitHub;

/**
 * @author  jbellmann
 */
public class IsAllowedOrgaSignupCondition extends GithubSignupCondition {

    private final Logger log = LoggerFactory.getLogger(IsAllowedOrgaSignupCondition.class);

    private final GithubSignupConditionProperties signupProperties;

    public IsAllowedOrgaSignupCondition(final GithubSignupConditionProperties signupProperties) {
        this.signupProperties = signupProperties;
    }

    @Override
    public boolean matches(final GitHub api) {
        if (signupProperties.getAllowedOrgas().isEmpty()) {
            return false;
        }

        if (signupProperties.getAllowedOrgas().contains(ALL_AUTHORIZED)) {
            return true;
        }

        log.info("RETURN ALLWAYS TRUE, IMPLEMENT ME! ORGAS: {}", signupProperties.getAllowedOrgas().toString());
        return true;
    }

}

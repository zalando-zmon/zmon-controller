package org.zalando.github.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.github.zmon.security.GithubSignupConditionProperties;
import org.zalando.github.zmon.security.IsAllowedOrgaSignupCondition;
import org.zalando.github.zmon.security.IsAllowedUserSignupCondition;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
public class GithubSignupConditionsConfig {

    @Autowired
    private GithubSignupConditionProperties signupProperties;

    @Bean
    public IsAllowedUserSignupCondition allowedUserSignupCondition() {
        return new IsAllowedUserSignupCondition(signupProperties);
    }

    @Bean
    public IsAllowedOrgaSignupCondition allowedOrgaSignupCondition() {
        return new IsAllowedOrgaSignupCondition(signupProperties);
    }
}

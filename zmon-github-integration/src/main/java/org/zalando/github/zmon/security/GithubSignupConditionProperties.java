package org.zalando.github.zmon.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author  jbellmann
 */
@ConfigurationProperties(prefix = "zmon.signup.github")
public class GithubSignupConditionProperties {

    private List<String> allowedUsers = new ArrayList<>(0);

    private List<String> allowedOrgas = new ArrayList<>(0);

    public List<String> getAllowedUsers() {
        return allowedUsers;
    }

    public void setAllowedUsers(final List<String> allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public List<String> getAllowedOrgas() {
        return allowedOrgas;
    }

    public void setAllowedOrgas(final List<String> allowedOrgas) {
        this.allowedOrgas = allowedOrgas;
    }

}

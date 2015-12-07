package org.zalando.github.zmon.service;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.github.api.GitHub;

/**
 * 
 * @author jbellmann
 *
 */
public class AccountConnectionSignupService implements ConnectionSignUp {

    private final Logger LOG = LoggerFactory.getLogger(AccountConnectionSignupService.class);

    private final UserDetailsManager userDetailsManager;

    public AccountConnectionSignupService(final UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public String execute(final Connection<?> connection) {

        Object api = connection.getApi();

        // use the api if you can
        if (api instanceof GitHub) {
            GitHub github = (GitHub) api;
            String login = github.userOperations().getProfileId();
        }

        // or use more generic
        org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();

        String username = profile.getUsername();

        LOG.info("Created user with id: " + username);

        User user = new User(username, "", Collections.emptyList());
        userDetailsManager.createUser(user);

        return username;
    }
}

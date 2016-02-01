package org.zalando.zmon.security.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import org.springframework.util.Assert;

import de.zalando.zmon.security.AuthorityService;

/**
 * @author jbellmann
 */
public class AccountConnectionSignupService implements ConnectionSignUp {

    private final Logger log = LoggerFactory.getLogger(AccountConnectionSignupService.class);

    private final UserDetailsManager userDetailsManager;
    private final AuthorityService authorityService;

    public AccountConnectionSignupService(final UserDetailsManager userDetailsManager,
                                          final AuthorityService authorityService) {
        Assert.notNull(userDetailsManager, "'userDetailsManager' should never be null");
        Assert.notNull(authorityService, "'authorityService' should never be null");
        this.userDetailsManager = userDetailsManager;
        this.authorityService = authorityService;
    }

    @Override
    public String execute(final Connection<?> connection) {

        if (!passesSignupConditions(connection)) {

            // returning 'null' fails the login-process
            return null;
        }

        // hwo to use api
        final String login = getLoginFromConnection(connection);

        // or use more generic
        final UserProfile profile = connection.fetchUserProfile();

        final String username = profile.getUsername();

        final Collection<? extends GrantedAuthority> authorities = authorityService.getAuthorities(username);

        if (authorities.isEmpty()) {
            log.info("Configure environment variable ZMON_AUTHORITIES_SIMPLE_USERS as * or {}", username);
            return null;
        }

        // we create an new user
        log.info("Creating user {}..", username);

        final User user = new User(username, "", authorities);
        userDetailsManager.createUser(user);

        return username;
    }

    protected boolean passesSignupConditions(final Connection<?> connection) {
        return true;
    }

    protected String getLoginFromConnection(final Connection<?> connection) {
        return "not_found";
    }
}

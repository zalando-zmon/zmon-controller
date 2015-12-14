package de.zalando.zmon.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * Get all {@link GrantedAuthority}s for an user.
 *
 * @author  jbellmann
 */
public interface AuthorityService {

    String ALL_AUTHORIZED = "*";

    Collection<? extends GrantedAuthority> getAuthorities(String username);

}

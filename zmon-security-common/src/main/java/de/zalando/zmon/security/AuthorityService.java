package de.zalando.zmon.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * Get all {@link GrantedAuthority}s for an user.
 * 
 * @author jbellmann
 *
 */
public interface AuthorityService {
	
	Collection<? extends GrantedAuthority> getAuthorities(String username);

}

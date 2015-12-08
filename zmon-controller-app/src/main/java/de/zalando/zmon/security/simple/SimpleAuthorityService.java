package de.zalando.zmon.security.simple;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import de.zalando.zmon.security.AuthorityService;
import de.zalando.zmon.security.authority.ZMonAdminAuthority;
import de.zalando.zmon.security.authority.ZMonAuthority;
import de.zalando.zmon.security.authority.ZMonLeadAuthority;
import de.zalando.zmon.security.authority.ZMonUserAuthority;

/**
 * 
 * @author jbellmann
 *
 */
public class SimpleAuthorityService implements AuthorityService {

	private final SimpleZmonAuthoritiesProperties authProperties;

	public SimpleAuthorityService(SimpleZmonAuthoritiesProperties authProperties) {
		Assert.notNull(authProperties, "'authProperties' should never be null");
		this.authProperties = authProperties;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(String username) {
		List<ZMonAuthority> result = Lists.newArrayList();
		if(authProperties.getAdmins().contains(username)){
			result = Lists.newArrayList(new ZMonAdminAuthority(username, ImmutableSet.of()));
		}
		if(authProperties.getLeads().contains(username)){
			result = Lists.newArrayList(new ZMonLeadAuthority(username, ImmutableSet.of()));
		}
		if(authProperties.getUsers().contains(username)){
			result = Lists.newArrayList(new ZMonUserAuthority(username, ImmutableSet.of()));
		}
		return result;
	}

}

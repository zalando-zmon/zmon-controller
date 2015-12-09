package de.zalando.zmon.security.simple;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import de.zalando.zmon.security.AuthorityService;
import de.zalando.zmon.security.TeamService;
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

	private final Logger log = LoggerFactory.getLogger(SimpleAuthorityService.class);

	private final SimpleZmonAuthoritiesProperties authProperties;
	private final TeamService teamService;

	public SimpleAuthorityService(SimpleZmonAuthoritiesProperties authProperties, TeamService teamService) {
		Assert.notNull(authProperties, "'authProperties' should never be null");
		Assert.notNull(teamService, "'teamService' should never be null");
		this.authProperties = authProperties;
		this.teamService = teamService;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(String username) {
		List<ZMonAuthority> result = Lists.newArrayList();
		ZMonAuthority authority = null;
		if(authProperties.getAdmins().contains(username)){
			authority = new ZMonAdminAuthority(username, ImmutableSet.copyOf(teamService.getTeams(username)));
		}else if(authProperties.getLeads().contains(username)){
			authority = new ZMonLeadAuthority(username, ImmutableSet.copyOf(teamService.getTeams(username)));
		}else if(authProperties.getUsers().contains(username)){
			authority = new ZMonUserAuthority(username, ImmutableSet.copyOf(teamService.getTeams(username)));
		}
		if(authority != null){
			result = Lists.newArrayList(authority);
			log.info("USER {} HAS AUTHORITY WITH ROLE {} AND TEAMS {}", username, authority.getAuthority(), authority.getTeams() );
		}
		return result;
	}

}

package org.zalando.zmon.security.simple;

import java.util.List;
import java.util.Set;

import org.zalando.zmon.security.TeamService;

import com.google.common.collect.Sets;

/**
 * 
 * @author jbellmann
 *
 */
public class SimpleTeamService implements TeamService {

	private final SimpleZmonTeamsProperties teamProperties;

	public SimpleTeamService(SimpleZmonTeamsProperties teamProperties) {
		this.teamProperties = teamProperties;
	}

	@Override
	public Set<String> getTeams(String username) {
		List<String> teams = teamProperties.getTeams().get(username);
		if (teams != null) {
			return Sets.newHashSet(teams);
		}
		return Sets.newHashSet();
	}

}

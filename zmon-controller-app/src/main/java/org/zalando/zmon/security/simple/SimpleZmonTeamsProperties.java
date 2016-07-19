package org.zalando.zmon.security.simple;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

/**
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "zmon.teams.simple")
public class SimpleZmonTeamsProperties {

	public String defaultTeam = null;

	public String getDefaultTeam() {
		return defaultTeam;
	}

	public void setDefaultTeam(String defaultTeam) {
		this.defaultTeam = defaultTeam;
	}

	private Map<String, List<String>> teams = Maps.newHashMap();

	public Map<String, List<String>> getTeams() {
		return teams;
	}

	public void setTeams(Map<String, List<String>> teams) {
		this.teams = teams;
	}

}

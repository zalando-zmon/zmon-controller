package de.zalando.zmon.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;

import de.zalando.zmon.security.AuthorityService;
import de.zalando.zmon.security.TeamService;
import de.zalando.zmon.security.simple.SimpleAuthorityService;
import de.zalando.zmon.security.simple.SimpleZmonAuthoritiesProperties;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@ConditionalOnMissingBean({ AuthorityService.class })
@EnableConfigurationProperties({ SimpleZmonAuthoritiesProperties.class })
public class SimpleAuthorityServiceConfiguration {

	@Autowired
	private SimpleZmonAuthoritiesProperties authProperties;

	@Autowired(required = false)
	private TeamService teamService = new NoOpTeamService();

	@Bean
	public AuthorityService simpleAuthorityService() {
		return new SimpleAuthorityService(authProperties, teamService);
	}

	// if there is no team-service
	private static class NoOpTeamService implements TeamService {

		@Override
		public Set<String> getTeams(String username) {
			return Sets.newHashSet();
		}
		
	}
}

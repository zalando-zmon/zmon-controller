package org.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.simple.SimpleTeamService;
import org.zalando.zmon.security.simple.SimpleZmonTeamsProperties;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@ConditionalOnMissingBean({TeamService.class})
@EnableConfigurationProperties({SimpleZmonTeamsProperties.class})
public class SimpleTeamServiceConfiguration {
	
	@Autowired
	private SimpleZmonTeamsProperties teamProperties;
	
	@Bean
	public SimpleTeamService simpleTeamService(){
		return new SimpleTeamService(teamProperties);
	}
}

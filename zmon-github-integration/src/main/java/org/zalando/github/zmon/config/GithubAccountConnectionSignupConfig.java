package org.zalando.github.zmon.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;
import org.zalando.github.zmon.security.GithubSignupCondition;
import org.zalando.github.zmon.service.GithubAccountConnectionSignupService;
import org.zalando.zmon.security.AuthorityService;
import org.zalando.zmon.security.service.AccountConnectionSignupService;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
public class GithubAccountConnectionSignupConfig {

	@Autowired
	private UserDetailsManager userDetailsManager;

	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private List<GithubSignupCondition> signupConditions;

	@Bean
	public AccountConnectionSignupService accountConnectionSignupService() {
		// compose all conditions into only one with 'and', all have to be 'true' to result in 'true'
		GithubSignupCondition signupCondition = GithubSignupCondition.and(signupConditions);
		return new GithubAccountConnectionSignupService(userDetailsManager, authorityService, signupCondition);
	}

}

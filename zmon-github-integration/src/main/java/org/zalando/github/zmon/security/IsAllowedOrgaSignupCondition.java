package org.zalando.github.zmon.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.github.api.GitHub;

/**
 * 
 * @author jbellmann
 *
 */
public class IsAllowedOrgaSignupCondition extends GithubSignupCondition {
	
	private final Logger log = LoggerFactory.getLogger(IsAllowedOrgaSignupCondition.class);
	
	private final GithubSignupConditionProperties signupProperties;
	
	public IsAllowedOrgaSignupCondition(GithubSignupConditionProperties signupProperties) {
		this.signupProperties = signupProperties;
	}

	@Override
	public boolean matches(GitHub api) {
		log.info("CHECK FOR ORGA ...");
		if(signupProperties.getAllowedOrgas().isEmpty()){
			return signupProperties.isAlwaysAllowWhenOrgasEmpty() ? true : false;
		}
		log.info("RETURN ALLWAYS TRUE, IMPLEMENT ME! ORGAS: {}", signupProperties.getAllowedOrgas().toString());
		return true;
	}

}

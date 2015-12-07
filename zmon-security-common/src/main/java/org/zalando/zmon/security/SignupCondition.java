package org.zalando.zmon.security;

import org.springframework.social.connect.Connection;

/**
 * A Condition that will be checked on signup to restrict users that can use the service.
 * 
 * @author jbellmann
 *
 */
public interface SignupCondition {
	
	boolean matches(Connection<?> connection);

	boolean supports(Class<?> clazz);

}

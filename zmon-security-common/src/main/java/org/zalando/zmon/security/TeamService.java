package org.zalando.zmon.security;

import java.util.Set;

/**
 * Get all teams of an user.
 * 
 * @author jbellmann
 *
 */
public interface TeamService {
	
	Set<String> getTeams(String username);

}

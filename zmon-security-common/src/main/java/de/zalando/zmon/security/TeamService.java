package de.zalando.zmon.security;

import java.util.Set;

/**
 * Teams-Lookup.
 * 
 * @author jbellmann
 *
 */
public interface TeamService {
	
	Set<String> getTeams(String username);

}

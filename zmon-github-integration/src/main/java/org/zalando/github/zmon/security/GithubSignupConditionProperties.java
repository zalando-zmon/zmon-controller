package org.zalando.github.zmon.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "zmon.signup.github")
public class GithubSignupConditionProperties {

	private boolean alwaysAllowWhenUsersEmtpy = true;

	private boolean alwaysAllowWhenOrgasEmpty = true;

	private List<String> allowedUsers = new ArrayList<>(0);

	private List<String> allowedOrgas = new ArrayList<>(0);

	public List<String> getAllowedUsers() {
		return allowedUsers;
	}

	public void setAllowedUsers(List<String> allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	public List<String> getAllowedOrgas() {
		return allowedOrgas;
	}

	public void setAllowedOrgas(List<String> allowedOrgas) {
		this.allowedOrgas = allowedOrgas;
	}

	public boolean isAlwaysAllowWhenUsersEmtpy() {
		return alwaysAllowWhenUsersEmtpy;
	}

	public void setAlwaysAllowWhenUsersEmtpy(boolean alwaysAllowWhenUsersEmtpy) {
		this.alwaysAllowWhenUsersEmtpy = alwaysAllowWhenUsersEmtpy;
	}

	public boolean isAlwaysAllowWhenOrgasEmpty() {
		return alwaysAllowWhenOrgasEmpty;
	}

	public void setAlwaysAllowWhenOrgasEmpty(boolean alwaysAllowWhenOrgasEmpty) {
		this.alwaysAllowWhenOrgasEmpty = alwaysAllowWhenOrgasEmpty;
	}

}

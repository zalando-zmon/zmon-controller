package org.zalando.zmon.security.simple;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Lists;

/**
 * 
 * @author jbellmann
 *
 */
@ConfigurationProperties(prefix = "zmon.authorities.simple")
public class SimpleZmonAuthoritiesProperties {

	private List<String> admins = Lists.newArrayList();

	private List<String> users = Lists.newArrayList();

	private List<String> leads = Lists.newArrayList();

	public List<String> getAdmins() {
		return admins;
	}

	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<String> getLeads() {
		return leads;
	}

	public void setLeads(List<String> leads) {
		this.leads = leads;
	}

	
}

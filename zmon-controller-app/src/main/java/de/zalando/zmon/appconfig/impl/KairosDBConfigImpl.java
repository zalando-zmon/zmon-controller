package de.zalando.zmon.appconfig.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import de.zalando.zmon.appconfig.KairosDBConfig;

/**
 * FIXME: hardcoded config for Vagrant box
 * 
 * @author hjacobs
 */
@Service
public class KairosDBConfigImpl implements KairosDBConfig {

	private final Environment environment;

	@Autowired
	public KairosDBConfigImpl(Environment environment) {
		this.environment = environment;
	}

	@Override
	public String getHost() {
		return environment.getProperty("zmon.kairosdb.host", "localhost");
	}

	@Override
	public Integer getPort() {
		return environment.getProperty("zmon.kairosdb.port", Integer.class, 38083);
	}

	@Override
	public Boolean isEnabled() {
		return environment.getProperty("zmon.kairosdb.isEnabled", Boolean.class, true);
	}

}

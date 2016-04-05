package org.zalando.zmon.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NoOpEventLog {
	
	private final Logger LOG = LoggerFactory.getLogger(NoOpEventLog.class);
	
	public void log(Object ... objects){
		LOG.warn(objects.toString());
	}

}

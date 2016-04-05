package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.security.authority.ZMonAuthority;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Base for {@link AlertDefinition}-funcitons.
 * 
 * @author jbellmann
 *
 */
abstract class AlertDefinitionPermission implements Function<ZMonAuthority, Boolean> {

	protected final AlertDefinition alertDefinition;
	
	AlertDefinitionPermission(AlertDefinition alertDefinition) {
		Preconditions.checkNotNull(alertDefinition, "'alertDefinition' should never be null");
		this.alertDefinition  = alertDefinition;
	}

}

package de.zalando.zmon.security.permission;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import de.zalando.zmon.domain.AlertDefinition;
import de.zalando.zmon.security.authority.ZMonAuthority;

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

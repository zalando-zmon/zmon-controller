package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.security.authority.ZMonAuthority;

class HasEditAlertDefinitionPermission extends AlertDefinitionPermission {

	HasEditAlertDefinitionPermission(AlertDefinition alertDefinition) {
		super(alertDefinition);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasEditAlertDefinitionPermission(alertDefinition);
	}

}

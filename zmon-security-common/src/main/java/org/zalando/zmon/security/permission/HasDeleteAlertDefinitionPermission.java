package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.security.authority.ZMonAuthority;

class HasDeleteAlertDefinitionPermission extends AlertDefinitionPermission {

	HasDeleteAlertDefinitionPermission(AlertDefinition alertDefinition) {
		super(alertDefinition);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasDeleteAlertDefinitionPermission(alertDefinition);
	}

}

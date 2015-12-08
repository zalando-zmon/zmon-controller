package de.zalando.zmon.security.legacy;

import de.zalando.zmon.domain.AlertDefinition;
import de.zalando.zmon.security.ZMonAuthority;

class HasAddAlertDefinitionPermission extends AlertDefinitionPermission {

	HasAddAlertDefinitionPermission(AlertDefinition alertDefinition) {
		super(alertDefinition);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasAddAlertDefinitionPermission(alertDefinition);
	}

}

package de.zalando.zmon.security.legacy;

import de.zalando.zmon.domain.AlertDefinition;
import de.zalando.zmon.security.ZMonAuthority;

class HasDeleteAlertDefinitionPermission extends AlertDefinitionPermission {

	HasDeleteAlertDefinitionPermission(AlertDefinition alertDefinition) {
		super(alertDefinition);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasDeleteAlertDefinitionPermission(alertDefinition);
	}

}

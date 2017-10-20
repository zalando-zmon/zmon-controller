package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.security.authority.ZMonAuthority;

class HasDeleteUnusedCheckDefinitionPermission extends UnusedCheckDefinitionPermission {

	HasDeleteUnusedCheckDefinitionPermission(CheckDefinition checkDefinition) {
		super(checkDefinition);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasDeleteUnusedCheckDefinitionPermission(checkDefinition);
	}

}

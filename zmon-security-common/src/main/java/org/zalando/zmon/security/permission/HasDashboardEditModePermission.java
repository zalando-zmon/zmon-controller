package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.DashboardImport;
import org.zalando.zmon.security.authority.ZMonAuthority;

class HasDashboardEditModePermission extends DashBoardPermission {

	HasDashboardEditModePermission(DashboardImport dashboard) {
		super(dashboard);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasDashboardEditModePermission(dashboard);
	}

}

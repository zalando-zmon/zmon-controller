package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.DashboardRecord;
import org.zalando.zmon.security.authority.ZMonAuthority;

class HasEditDashboardPermission extends DashBoardPermission {

	HasEditDashboardPermission(DashboardRecord dashboard) {
		super(dashboard);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasEditDashboardPermission(dashboard);
	}

}

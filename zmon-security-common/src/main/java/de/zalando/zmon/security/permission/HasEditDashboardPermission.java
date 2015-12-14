package de.zalando.zmon.security.permission;

import de.zalando.zmon.domain.Dashboard;
import de.zalando.zmon.security.authority.ZMonAuthority;

class HasEditDashboardPermission extends DashBoardPermission {

	HasEditDashboardPermission(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public Boolean apply(ZMonAuthority input) {
		return input.hasEditDashboardPermission(dashboard);
	}

}

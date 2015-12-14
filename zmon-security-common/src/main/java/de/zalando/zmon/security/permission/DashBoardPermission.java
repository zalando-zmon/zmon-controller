package de.zalando.zmon.security.permission;

import com.google.common.base.Function;

import de.zalando.zmon.domain.Dashboard;
import de.zalando.zmon.security.authority.ZMonAuthority;

abstract class DashBoardPermission implements Function<ZMonAuthority, java.lang.Boolean> {

	protected final Dashboard dashboard;

	DashBoardPermission(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

}

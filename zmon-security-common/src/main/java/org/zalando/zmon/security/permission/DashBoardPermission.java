package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.Dashboard;
import org.zalando.zmon.security.authority.ZMonAuthority;

import com.google.common.base.Function;

abstract class DashBoardPermission implements Function<ZMonAuthority, java.lang.Boolean> {

	protected final Dashboard dashboard;

	DashBoardPermission(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

}

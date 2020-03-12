package org.zalando.zmon.security.permission;

import org.zalando.zmon.domain.DashboardRecord;
import org.zalando.zmon.security.authority.ZMonAuthority;

import com.google.common.base.Function;

abstract class DashBoardPermission implements Function<ZMonAuthority, java.lang.Boolean> {

	protected final DashboardRecord dashboard;

	DashBoardPermission(DashboardRecord dashboard) {
		this.dashboard = dashboard;
	}

}

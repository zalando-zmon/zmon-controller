package org.zalando.zmon.service;

import java.util.List;

import org.zalando.zmon.domain.Dashboard;
import org.zalando.zmon.exception.ZMonException;

public interface DashboardService {

    List<Dashboard> getDashboards(List<Integer> dashboardIds);

    List<Dashboard> getAllDashboards();

    Dashboard createOrUpdateDashboard(Dashboard dashboard) throws ZMonException;

    void deleteDashboard(Integer dashboardId);

}

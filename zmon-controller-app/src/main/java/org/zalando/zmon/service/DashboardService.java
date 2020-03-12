package org.zalando.zmon.service;

import java.util.List;

import org.zalando.zmon.domain.DashboardRecord;
import org.zalando.zmon.exception.ZMonException;

public interface DashboardService {

    List<DashboardRecord> getDashboards(List<Integer> dashboardIds);

    List<DashboardRecord> getAllDashboards();

    DashboardRecord createOrUpdateDashboard(DashboardRecord dashboard) throws ZMonException;

    void deleteDashboard(Integer dashboardId);

}

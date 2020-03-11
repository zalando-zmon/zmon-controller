package org.zalando.zmon.service;

import java.util.List;

import org.zalando.zmon.domain.DashboardImport;
import org.zalando.zmon.exception.ZMonException;

public interface DashboardService {

    List<DashboardImport> getDashboards(List<Integer> dashboardIds);

    List<DashboardImport> getAllDashboards();

    DashboardImport createOrUpdateDashboard(DashboardImport dashboard) throws ZMonException;

    void deleteDashboard(Integer dashboardId);

}

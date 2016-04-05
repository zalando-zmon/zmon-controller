package org.zalando.zmon.persistence;

import java.util.List;

import org.zalando.zmon.domain.Dashboard;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

@SProcService
public interface DashboardSProcService {

    @SProcCall
    List<Dashboard> getDashboards(@SProcParam List<Integer> dashboardIds);

    @SProcCall
    List<Dashboard> getAllDashboards();

    @SProcCall
    DashboardOperationResult createOrUpdateDashboard(@SProcParam Dashboard dashboard);

    @SProcCall
    void deleteDashboard(@SProcParam Integer dashboardId);

}

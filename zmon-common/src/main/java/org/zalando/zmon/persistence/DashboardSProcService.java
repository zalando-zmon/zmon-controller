package org.zalando.zmon.persistence;

import java.util.List;

import org.zalando.zmon.domain.DashboardRecord;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

@SProcService
public interface DashboardSProcService {

    @SProcCall
    List<DashboardRecord> getDashboardRecords(@SProcParam List<Integer> dashboardIds);

    @SProcCall
    List<DashboardRecord> getAllDashboardRecords();

    @SProcCall
    DashboardOperationResult createOrUpdateDashboardRecord(@SProcParam DashboardRecord dashboard);

    @SProcCall
    void deleteDashboard(@SProcParam Integer dashboardId);

}

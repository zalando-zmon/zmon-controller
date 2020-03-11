package org.zalando.zmon.persistence;

import java.util.List;

import org.zalando.zmon.domain.DashboardImport;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

@SProcService
public interface DashboardSProcService {

    @SProcCall
    List<DashboardImport> getDashboardImports(@SProcParam List<Integer> dashboardIds);

    @SProcCall
    List<DashboardImport> getAllDashboardImports();

    @SProcCall
    DashboardOperationResult createOrUpdateDashboardImport(@SProcParam DashboardImport dashboard);

    @SProcCall
    void deleteDashboardImport(@SProcParam Integer dashboardId);

}

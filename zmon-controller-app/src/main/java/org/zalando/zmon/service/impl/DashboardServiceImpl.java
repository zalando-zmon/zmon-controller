package org.zalando.zmon.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.domain.Dashboard;
import org.zalando.zmon.event.ZMonEventType;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.persistence.DashboardOperationResult;
import org.zalando.zmon.persistence.DashboardSProcService;
import org.zalando.zmon.service.DashboardService;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private static final Comparator<Dashboard> DASHBOARD_ID_COMPARATOR = (o1, o2) -> Ints.compare(o1.getId(), o2.getId());

    private final NoOpEventLog eventLog;

    private final DashboardSProcService dashboardSProc;

    @Autowired
    public DashboardServiceImpl(NoOpEventLog eventLog, DashboardSProcService dashboardSProc) {
        this.eventLog = eventLog;
        this.dashboardSProc = dashboardSProc;
    }

    @Override
    public List<Dashboard> getDashboards(final List<Integer> dashboardIds) {
        final List<Dashboard> dashboards = dashboardSProc.getDashboards(dashboardIds);
        Collections.sort(dashboards, DASHBOARD_ID_COMPARATOR);

        return dashboards;
    }

    @Override
    public List<Dashboard> getAllDashboards() {
        final List<Dashboard> dashboards = dashboardSProc.getAllDashboards();
        Collections.sort(dashboards, DASHBOARD_ID_COMPARATOR);

        return dashboards;
    }

    @Override
    public Dashboard createOrUpdateDashboard(final Dashboard dashboard) throws ZMonException {
        Preconditions.checkNotNull(dashboard);
        log.info("Saving dashboard '{}' request from user '{}'", dashboard.getId(), dashboard.getLastModifiedBy());

        final DashboardOperationResult result = dashboardSProc.createOrUpdateDashboard(dashboard)
                                                              .throwExceptionOnFailure();
        final Dashboard entity = result.getEntity();

        eventLog.log(dashboard.getId() == null ? ZMonEventType.DASHBOARD_CREATED : ZMonEventType.DASHBOARD_UPDATED,
            entity.getId(), entity.getName(), entity.getWidgetConfiguration(), entity.getAlertTeams(),
            entity.getViewMode(), entity.getEditOption(), entity.getSharedTeams(), entity.getLastModifiedBy());

        return entity;
    }

    @Override
    public void deleteDashboard(final Integer dashboardId) {
        Preconditions.checkNotNull(dashboardId);
        log.info("Delete dashboard '{}' request from user '{}'", dashboardId);

        dashboardSProc.deleteDashboard(dashboardId);
    }
}

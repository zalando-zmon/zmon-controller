package org.zalando.zmon.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.zmon.domain.DashboardRecord;
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

    private static final Comparator<DashboardRecord> DASHBOARD_ID_COMPARATOR = (o1, o2) -> Ints.compare(o1.getId(), o2.getId());

    private final NoOpEventLog eventLog;

    private final DashboardSProcService dashboardSProc;

    @Autowired
    public DashboardServiceImpl(NoOpEventLog eventLog, DashboardSProcService dashboardSProc) {
        this.eventLog = eventLog;
        this.dashboardSProc = dashboardSProc;
    }

    @Override
    public List<DashboardRecord> getDashboards(final List<Integer> dashboardIds) {
        final List<DashboardRecord> dashboards = dashboardSProc.getDashboardRecords(dashboardIds);
        Collections.sort(dashboards, DASHBOARD_ID_COMPARATOR);

        return dashboards;
    }

    @Override
    public List<DashboardRecord> getAllDashboards() {
        final List<DashboardRecord> dashboards = dashboardSProc.getAllDashboardRecords();
        Collections.sort(dashboards, DASHBOARD_ID_COMPARATOR);

        return dashboards;
    }

    @Override
    public DashboardRecord createOrUpdateDashboard(final DashboardRecord dashboard) throws ZMonException {
        Preconditions.checkNotNull(dashboard);
        log.info("Saving dashboard '{}' request from user '{}'", dashboard.getId(), dashboard.getLastModifiedBy());

        final DashboardOperationResult result = dashboardSProc.createOrUpdateDashboardRecord(dashboard)
                                                              .throwExceptionOnFailure();
        final DashboardRecord entity = result.getEntity();

        eventLog.log(dashboard.getId() == null ? ZMonEventType.DASHBOARD_CREATED : ZMonEventType.DASHBOARD_UPDATED,
            entity.getId(), entity.getName(), entity.getWidgetConfiguration(), entity.getAlertTeams(),
            entity.getViewMode(), entity.getEditOption(), entity.getSharedTeams(), entity.getLastModifiedBy());

        return entity;
    }

    @Override
    public void deleteDashboard(final Integer dashboardId) {
        Preconditions.checkNotNull(dashboardId);
        log.info("Delete dashboard '{}' request from user '{}'", dashboardId);

        dashboardSProc.deleteDashboardRecord(dashboardId);
    }
}

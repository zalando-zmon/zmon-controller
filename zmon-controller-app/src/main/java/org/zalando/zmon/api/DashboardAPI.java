package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.domain.DashboardRecord;
import org.zalando.zmon.domain.EditOption;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.api.domain.DashboardRest;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.DashboardService;

import java.util.List;

/**
 * Created by jmussler on 3/10/15.
 */

@Controller
@RequestMapping("/api/v1/dashboard")
public class DashboardAPI {

    @Autowired
    DefaultZMonPermissionService authorityService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private DashboardService service;

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public int postDashboard(@RequestBody DashboardRest dashboardRest) throws ZMonException {

        DashboardRecord dashboard = dashboardRest.toDashboard(mapper);

        final String currentUser = authorityService.getUserName();
        if (dashboard.getId() == null) {
            dashboard.setCreatedBy(currentUser);
        }

        dashboard.setLastModifiedBy(currentUser);

        // check security
        authorityService.verifyEditDashboardPermission(dashboard);

        // if dashboard is shared with the team, set user teams
        final List<String> sharedTeams = dashboard.getEditOption() == EditOption.TEAM
                ? ImmutableList.copyOf(authorityService.getTeams()) : ImmutableList.of();
        dashboard.setSharedTeams(sharedTeams);

        return service.createOrUpdateDashboard(dashboard).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public int postDashboard(@RequestBody DashboardRest dashboardRest, @PathVariable(value="id") int id) throws ZMonException {

        if(dashboardRest.id == null) {
            throw new ZMonException("JSON does not contain dashboard id, don't use this to create new dashboard!");
        }
        else if(dashboardRest.id != id) {
            throw new ZMonException("Dashboard ID in JSON and path does not match!");
        }

        DashboardRecord dashboard = dashboardRest.toDashboard(mapper);

        final String currentUser = authorityService.getUserName();
        if (dashboard.getId() == null) {
            dashboard.setCreatedBy(currentUser);
        }

        dashboard.setLastModifiedBy(currentUser);

        // check security
        authorityService.verifyEditDashboardPermission(dashboard);

        // if dashboard is shared with the team, set user teams
        final List<String> sharedTeams = dashboard.getEditOption() == EditOption.TEAM
                ? ImmutableList.copyOf(authorityService.getTeams()) : ImmutableList.of();
        dashboard.setSharedTeams(sharedTeams);

        return service.createOrUpdateDashboard(dashboard).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}")
    public DashboardRest getDashboard(@PathVariable(value="id") int dashboardId) {
        final List<DashboardRecord> dashboards = service.getDashboards(Lists.newArrayList(dashboardId));

        if (dashboards.isEmpty()) {
            return null;
        }

        DashboardRest dr = DashboardRest.from(dashboards.get(0), mapper);
        return dr;
    }
}

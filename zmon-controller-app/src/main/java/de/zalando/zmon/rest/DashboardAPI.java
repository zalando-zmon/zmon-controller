package de.zalando.zmon.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.zalando.zmon.domain.Dashboard;
import de.zalando.zmon.domain.EditOption;
import de.zalando.zmon.exception.ZMonException;
import de.zalando.zmon.rest.domain.DashboardRest;
import de.zalando.zmon.security.legacy.DefaultZMonPermissionService;
import de.zalando.zmon.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

        Dashboard dashboard = dashboardRest.toDashboard(mapper);

        final String currentUser = authorityService.getUserName();
        if (dashboard.getId() == null) {
            dashboard.setCreatedBy(currentUser);
        }

        dashboard.setLastModifiedBy(currentUser);

        // check security
        authorityService.verifyEditDashboardPermission(dashboard);

        // if dashboard is shared with the team, set user teams
        final List<String> sharedTeams = dashboard.getEditOption() == EditOption.TEAM
                ? ImmutableList.copyOf(authorityService.getTeams()) : ImmutableList.<String>of();
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

        Dashboard dashboard = dashboardRest.toDashboard(mapper);

        final String currentUser = authorityService.getUserName();
        if (dashboard.getId() == null) {
            dashboard.setCreatedBy(currentUser);
        }

        dashboard.setLastModifiedBy(currentUser);

        // check security
        authorityService.verifyEditDashboardPermission(dashboard);

        // if dashboard is shared with the team, set user teams
        final List<String> sharedTeams = dashboard.getEditOption() == EditOption.TEAM
                ? ImmutableList.copyOf(authorityService.getTeams()) : ImmutableList.<String>of();
        dashboard.setSharedTeams(sharedTeams);

        return service.createOrUpdateDashboard(dashboard).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public DashboardRest getDashboard(@PathVariable(value="id") int dashboardId) {
        final List<Dashboard> dashboards = service.getDashboards(Lists.newArrayList(dashboardId));

        if (dashboards.isEmpty()) {
            return null;
        }

        DashboardRest dr = DashboardRest.from(dashboards.get(0), mapper);
        return dr;
    }
}

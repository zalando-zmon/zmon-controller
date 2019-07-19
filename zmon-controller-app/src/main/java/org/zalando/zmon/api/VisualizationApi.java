package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.VisualizationService;

import java.util.Map;

@Controller
@RequestMapping(value = "/api/v1/visualization")
public class VisualizationApi {

    private VisualizationService visualizationService;
    private DefaultZMonPermissionService authService;

    @Autowired
    public VisualizationApi(VisualizationService visualizationService,
                            DefaultZMonPermissionService authService) {
        this.visualizationService = visualizationService;
        this.authService = authService;
    }

    @RequestMapping(value = "/")
    public String visualizationHomeRedirect() {
        return visualizationService.homeRedirect();
    }

    @RequestMapping(value = "/script")
    public String grafanaScriptedDashboardRedirect(@RequestParam Map<String, String> params) {
        return visualizationService.dynamicDashboardRedirect(params);
    }

    @ResponseBody
    @RequestMapping(value = "/dashboard/{id}", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getDashboard(@PathVariable(value = "id") String id) {
        if (!authService.hasUserAuthority()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return visualizationService.getDashboard(id);
    }

    @ResponseBody
    @RequestMapping(value = "/dashboard/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<JsonNode> deleteDashboard(@PathVariable(value = "id") String id) {
        if (!authService.hasUserAuthority()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return visualizationService.deleteDashboard(id);
    }

    @ResponseBody
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST)
    public ResponseEntity<JsonNode> upsertDashboard(@RequestBody(required = true) String body) {
        if (!authService.hasUserAuthority()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return visualizationService.upsertDashboard(body);
    }

    @ResponseBody
    @RequestMapping(value = "/dashboard/search", method = RequestMethod.DELETE)
    public ResponseEntity<JsonNode> searchDashboards(
            @RequestParam(name = "query", defaultValue = "") String query,
            @RequestParam(name = "limit", defaultValue = "25") int limit) {
        if (!authService.hasUserAuthority()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return visualizationService.searchDashboards(query, limit);
    }
}

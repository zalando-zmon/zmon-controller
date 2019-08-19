package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.api.domain.ResourceNotFoundException;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;
import org.zalando.zmon.service.VisualizationService;
import org.zalando.zmon.service.impl.Grafana;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jmussler on 3/25/15.
 */

@Controller
@RequestMapping("/api/v1/grafana2-dashboards")
public class Grafana2RestAPI {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    VisualizationService visualizationService;

    @Autowired
    GrafanaDashboardSprocService grafanaDashboardSprocService;


    private final Logger log = LoggerFactory.getLogger(Grafana.class);


    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getDashboard(@PathVariable(value = "id") String id, @RequestHeader("Authorization") String authHeader) throws ZMonException {
        if (id.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        String uid = grafanaDashboardSprocService.getGrafanaMapping(id);
        uid = null == uid || uid.isEmpty() ? id : uid;
        return visualizationService.getDashboard(uid, extractToken(authHeader));
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<JsonNode> saveDashboard(@RequestBody(required = true) final JsonNode grafanaData,
                                                  @RequestHeader("Authorization") String authHeader) throws IOException {

        String dashboard = mapper.writeValueAsString(grafanaData.get("dashboard"));
        dashboard = "{\"dashboard\": " + dashboard + "}";
        return visualizationService.upsertDashboard(dashboard, extractToken(authHeader));
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> g2getDashboards(@RequestParam Map<String, String> params,
                                                    @RequestHeader("Authorization") String authHeader) {
        return visualizationService.searchDashboards(params, extractToken(authHeader));
    }

    private String extractToken(String authHeader) {
        String[] auth = authHeader.split(" ");
        return auth[1];
    }
}

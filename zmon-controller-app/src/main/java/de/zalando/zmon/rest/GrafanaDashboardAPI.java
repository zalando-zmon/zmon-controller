package de.zalando.zmon.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.zalando.zmon.exception.ZMonException;
import de.zalando.zmon.persistence.GrafanaDashboardSprocService;
import de.zalando.zmon.security.permission.DefaultZMonPermissionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by jmussler on 3/25/15.
 */

@Controller
@RequestMapping("/api/v1/grafana")
public class GrafanaDashboardAPI {

    @Autowired
    DefaultZMonPermissionService authService;

    @Autowired
    GrafanaDashboardSprocService grafanaService;

    @Autowired
    ObjectMapper mapper;

    private Logger log = LoggerFactory.getLogger(GrafanaDashboardAPI.class);

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void putDashboard(@PathVariable(value="id") String id, @RequestBody JsonNode grafanaData) throws ZMonException, JsonProcessingException {
        String title = grafanaData.get("title").asText();
        String dashboard = mapper.writeValueAsString(grafanaData.get("dashboard"));

        log.info("Saving Grafana dashboard \"{}\"..", title);
        grafanaService.createOrUpdateGrafanaDashboard(id, title, dashboard, authService.getUserName());
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonNode getDashboard(@PathVariable(value="id") String id) throws ZMonException, IOException {
        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboard(id);
        if(dashboards.isEmpty()) {
            log.info("No Grafana dashboard found for id {}", id);
            return null;
        }

        ObjectNode node = mapper.createObjectNode();
        ObjectNode sourceNode = mapper.createObjectNode();

        node.put("found", true);
        node.put("_type", "dashboard");
        node.put("_id", id);
        node.put("_source", sourceNode);
        sourceNode.put("title", dashboards.get(0).title);
        sourceNode.put("tags", mapper.createArrayNode());
        sourceNode.put("dashboard", mapper.readTree(dashboards.get(0).dashboard));

        return node;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public JsonNode getDashboards(@RequestBody JsonNode grafanaSearch) throws ZMonException {
        ObjectNode r = mapper.createObjectNode();
        ArrayNode hits = mapper.createArrayNode();
        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboards();
        for(GrafanaDashboardSprocService.GrafanaDashboard d : dashboards) {
            ObjectNode hit = mapper.createObjectNode();
            hit.put("_id", d.title);
            hit.put("_type", "dashboard");
            hits.add(hit);
        }
        return r;
    }
}

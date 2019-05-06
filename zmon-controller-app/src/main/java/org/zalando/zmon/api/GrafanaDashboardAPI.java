package org.zalando.zmon.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import java.io.IOException;
import java.util.List;

/**
 * Created by jmussler on 3/25/15.
 */

@Controller
@RequestMapping("/api/v1/grafana")
public class GrafanaDashboardAPI {

    DefaultZMonPermissionService authService;
    GrafanaDashboardSprocService grafanaService;
    ObjectMapper mapper;

    @Autowired
    public GrafanaDashboardAPI(GrafanaDashboardSprocService grafanaDashboardSprocService, ObjectMapper mapper, DefaultZMonPermissionService authService) {
        this.authService = authService;
        this.grafanaService = grafanaDashboardSprocService;
        this.mapper = mapper;
    }

    private Logger log = LoggerFactory.getLogger(GrafanaDashboardAPI.class);

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void putDashboard(@PathVariable(value="id") String id, @RequestBody JsonNode grafanaData) throws ZMonException, JsonProcessingException {
        String title = grafanaData.get("title").asText();
        String dashboard = mapper.writeValueAsString(grafanaData.get("dashboard"));

        log.info("Saving Grafana dashboard \"{}\"..", title);
        grafanaService.createOrUpdateGrafanaDashboard(id, title, dashboard, authService.getUserName(), "v1");
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonNode getDashboard(@PathVariable(value="id") String id) throws ZMonException, IOException {
        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboard(id, authService.getUserName());
        if(dashboards.isEmpty()) {
            log.info("No Grafana dashboard found for id {}", id);
            return null;
        }

        ObjectNode node = mapper.createObjectNode();
        ObjectNode sourceNode = mapper.createObjectNode();

        node.put("found", true);
        node.put("_type", "dashboard");
        node.put("_id", id);
        node.set("_source", sourceNode);
        sourceNode.put("title", dashboards.get(0).title);
        sourceNode.set("tags", mapper.createArrayNode());
        sourceNode.set("dashboard", mapper.readTree(dashboards.get(0).dashboard));

        return node;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public List<String> deleteDashboard(@PathVariable(value="id") String id) throws ZMonException, IOException {
        return grafanaService.deleteGrafanaDashboard(id, authService.getUserName());
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/_search", method = RequestMethod.POST)
    public JsonNode getDashboards(@RequestBody JsonNode grafanaSearch) throws ZMonException {
        ObjectNode r = mapper.createObjectNode();
        ArrayNode hits = mapper.createArrayNode();

        JsonNode query = grafanaSearch.get("query").get("query_string").get("query");
        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboards(query.textValue().replace("title:", "").replace("*", ""), null, null, null);

        for(GrafanaDashboardSprocService.GrafanaDashboard d : dashboards) {
            ObjectNode hit = mapper.createObjectNode();
            hit.put("_id", d.title);
            hit.put("_type", "dashboard");
            hits.add(hit);
        }

        r.set("hits", hits);
        return r;
    }
}

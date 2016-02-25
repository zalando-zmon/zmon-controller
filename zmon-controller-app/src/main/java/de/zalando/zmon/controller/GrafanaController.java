package de.zalando.zmon.controller;

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

@Controller
@RequestMapping(value = "/rest/grafana")
public class GrafanaController extends AbstractZMonController {

    private final Logger log = LoggerFactory.getLogger(GrafanaController.class);

    @Autowired
    GrafanaDashboardSprocService grafanaService;

    @Autowired
    DefaultZMonPermissionService authService;

    @Autowired
    ObjectMapper mapper;




    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/dashboard/{id}", method = RequestMethod.PUT)
    public void putDashboard(@PathVariable(value = "id") String id, @RequestBody JsonNode grafanaData) throws ZMonException, JsonProcessingException {
        String title = grafanaData.get("title").asText();
        String dashboard = grafanaData.get("dashboard").asText();

        log.info("Saving Grafana dashboard \"{}\"..", title);
        grafanaService.createOrUpdateGrafanaDashboard(id, title, dashboard, authService.getUserName(), "v1");
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/dashboard/{id}", method = RequestMethod.GET)
    public JsonNode getDashboard(@PathVariable(value = "id") String id) throws ZMonException {
        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboard(id, authService.getUserName());
        if (dashboards.isEmpty()) {
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
        sourceNode.put("dashboard", dashboards.get(0).dashboard);

        return node;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/dashboard/{id}", method = RequestMethod.DELETE)
    public List<String> deleteDashboard(@PathVariable(value="id") String id) throws ZMonException, IOException {
        return grafanaService.deleteGrafanaDashboard(id, authService.getUserName());
    }


    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/dashboard/_search", method = RequestMethod.POST)
    public JsonNode getDashboards(@RequestBody JsonNode grafanaSearch) throws ZMonException {
        ObjectNode r = mapper.createObjectNode();
        ObjectNode hits = mapper.createObjectNode();
        ObjectNode facets = mapper.createObjectNode();
        ObjectNode facetsTags = mapper.createObjectNode();
        ArrayNode facetsTagsTerms = mapper.createArrayNode();
        ArrayNode hitsHits = mapper.createArrayNode();

        JsonNode query = grafanaSearch.get("query").get("query_string").get("query");
        List<GrafanaDashboardSprocService.GrafanaDashboard> dashboards = grafanaService.getGrafanaDashboards(query.textValue().replace("title:", "").replace("*", ""), null, null, null);

        for (GrafanaDashboardSprocService.GrafanaDashboard d : dashboards) {

            ObjectNode hit = mapper.createObjectNode();
            ObjectNode source = mapper.createObjectNode();

            hit.put("_id", d.id);
            hit.put("_type", "dashboard");
            hit.put("_source", source);

            source.put("dashboard", "");
            source.put("tags", mapper.createArrayNode());
            source.put("title", d.title);
            source.put("user", d.user);
            source.put("group", d.user);

            hitsHits.add(hit);

        }

        facetsTags.put("_type", "terms");
        facetsTags.put("missing", 0);
        facetsTags.put("other", 0);
        facetsTags.put("total", hitsHits.size());
        facetsTags.put("terms", facetsTagsTerms);
        facets.put("tags", facetsTags);
        hits.put("total", dashboards.size());
        hits.put("hits", hitsHits);
        r.put("hits", hits);
        r.put("facets", facets);
        r.put("timed_out", false);

        return r;
    }
}

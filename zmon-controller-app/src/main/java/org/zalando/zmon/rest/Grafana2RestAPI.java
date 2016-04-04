package org.zalando.zmon.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.controller.Grafana2Controller;
import org.zalando.zmon.exception.ZMonException;

import java.io.IOException;
import java.util.List;

/**
 * Created by jmussler on 3/25/15.
 */

@Controller
@RequestMapping("/api/v1/grafana2-dashboards")
public class Grafana2RestAPI {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    Grafana2Controller grafana2UI;

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getDashboard(@PathVariable(value = "id") String id) throws ZMonException, IOException {
        return grafana2UI.getDashboard(id);
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<JsonNode> saveDashboard(@RequestBody(required = true) final JsonNode grafanaData) throws IOException {
        return grafana2UI.saveDashboard(grafanaData);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public JsonNode g2getDashboards(@RequestParam(value = "query", required = false) String query, @RequestParam(value = "tag", required = false) List<String> tags, @RequestParam(value = "starred", defaultValue = "false") boolean starred) throws IOException, ZMonException {
        return grafana2UI.searchDashboards(query, tags, starred);
    }
}

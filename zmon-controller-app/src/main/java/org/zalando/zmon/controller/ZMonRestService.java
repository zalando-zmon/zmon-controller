package org.zalando.zmon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.api.SearchAPI;
import org.zalando.zmon.api.domain.EntityFilterRequest;
import org.zalando.zmon.api.domain.EntityFilterResponse;
import org.zalando.zmon.api.domain.EntityObject;
import org.zalando.zmon.config.MetricCacheProperties;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.api.EntityApi;
import org.zalando.zmon.api.domain.CheckChartResult;
import org.zalando.zmon.persistence.CheckDefinitionImportResult;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.ZMonService;
import org.zalando.zmon.exception.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/rest")
public class ZMonRestService extends AbstractZMonController {

    private final Logger log = LoggerFactory.getLogger(ZMonRestService.class);

    private final ZMonService service;

    private final MetricCacheProperties metricCacheProperties;

    private final EntityApi entityApi;

    private final DefaultZMonPermissionService authService;

    private final SearchAPI searchAPI;

    private final ObjectMapper mapper;

    private final Executor executor;

    @Autowired
    public ZMonRestService(ZMonService service,
                           MetricCacheProperties metricCacheProperties,
                           EntityApi entityApi,
                           DefaultZMonPermissionService authService,
                           SearchAPI searchAPI,
                           ObjectMapper mapper,
                           @Qualifier("metricCacheHttpClient") HttpClient httpClient) {
        this.service = service;
        this.metricCacheProperties = metricCacheProperties;
        this.entityApi = entityApi;
        this.authService = authService;
        this.searchAPI = searchAPI;
        this.mapper = mapper;
        this.executor = Executor.newInstance(httpClient);
    }

    @GetMapping(value = "/status")
    public ResponseEntity<ExecutionStatus> getStatus() {
        return new ResponseEntity<>(service.getStatus(), HttpStatus.OK);
    }

    @RequestMapping(value = "/allTeams")
    public ResponseEntity<List<String>> getAllTeams() {
        return new ResponseEntity<>(service.getAllTeams(), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkDefinitions")
    public ResponseEntity<List<CheckDefinition>> getAllCheckDefinitions(
            @RequestParam(value = "team", required = false) final Set<String> teams) {
        final List<CheckDefinition> defs = teams == null ? service.getCheckDefinitions(null).getCheckDefinitions()
                : service.getCheckDefinitions(null, teams);

        return new ResponseEntity<>(defs, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateCheckDefinition")
    public ResponseEntity<CheckDefinition> updateCheckDefinition(@RequestBody CheckDefinitionImport check) throws ZMonException {
        if (check.getOwningTeam() == null || "".equals(check.getOwningTeam())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CheckDefinitionImportResult result = service.createOrUpdateCheckDefinition(check, authService.getUserName(), Lists.newArrayList(authService.getTeams()), authService.hasAdminAuthority());
        if (result.isPermissionDenied()) {
            throw new CheckPermissionDeniedException("Access to check denied. Please check your team permissions!");
        }

        return new ResponseEntity<>(result.getEntity(), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkDefinition")
    public ResponseEntity<CheckDefinition> getCheckDefinition (
            @RequestParam(value = "check_id") final int checkId) throws ZMonException{

        final List<CheckDefinition> checkDefinitions = service.getCheckDefinitions(null, Lists.newArrayList(checkId));
        if (checkDefinitions.isEmpty()) {
            throw new CheckDefinitionNotFoundException("Check ID not found. Try again with a valid check Id!");
        }

        return new ResponseEntity<>(checkDefinitions.get(0), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkResults")
    public ResponseEntity<List<CheckResults>> getCheckResults(
            @RequestParam(value = "check_id") final int checkId,
            @RequestParam(value = "entity", required = false) final String entity,
            @RequestParam(value = "limit", defaultValue = "20") final int limit) {

        return new ResponseEntity<>(service.getCheckResults(checkId, entity, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkResultsWithoutEntities")
    public ResponseEntity<List<CheckResults>> getCheckResultsWithoutEntities(
            @RequestParam(value = "check_id") final int checkId,
            @RequestParam(value = "entity") final String entity,
            @RequestParam(value = "limit", defaultValue = "20") final int limit) {

        return new ResponseEntity<>(service.getCheckResultsWithoutEntities(checkId, entity, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "checkResultsChart")
    public ResponseEntity<CheckChartResult> getChartResults(
            @RequestParam(value = "check_id") final int checkId,
            @RequestParam(value = "entity", required = false) final String entity,
            @RequestParam(value = "limit", defaultValue = "20") final int limit) {
        return new ResponseEntity<>(service.getChartResults(checkId, entity, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkAlertResults")
    public ResponseEntity<List<CheckResults>> getCheckAlertResults(
            @RequestParam(value = "alert_id") final int alertId,
            @RequestParam(value = "limit", defaultValue = "20") final int limit) {
        return new ResponseEntity<>(service.getCheckAlertResults(alertId, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "/entityProperties")
    public ResponseEntity<JsonNode> getEntityProperties() {
        return new ResponseEntity<>(service.getEntityProperties(), HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "/entities")
    public List<EntityObject> getEntities(@RequestParam(value = "query", defaultValue = "{}") String data, @RequestParam(value = "exclude", defaultValue="") String exclude) throws IOException {
        return entityApi.getEntities(data, exclude);
    }

    @ResponseBody
    @RequestMapping(value = "/entities", method = RequestMethod.POST)
    public List<EntityObject> getEntitiesPost(@RequestBody JsonNode node) throws IOException {
        return entityApi.getEntities(mapper.writeValueAsString(node), "");
    }


    @ResponseBody
    @RequestMapping(value = "cloud-view-endpoints", produces = "application/json")
    public void cloudViewEndpoints(@RequestParam(value = "application_id") String applicationId, final Writer writer,
                                   final HttpServletResponse response) throws IOException {
        final String dataServiceQuery = metricCacheProperties.getUrl() + "/api/v1/rest-api-metrics/kairosdb-format?application_id=" + applicationId;

        int nodeId = Math.abs(applicationId.hashCode() % metricCacheProperties.getNodes());
        final String r = executor.execute(Request.Get(dataServiceQuery).addHeader("Cookie", "metric_cache=" + nodeId)).returnContent().asString();

        response.setContentType("application/json");
        writer.write(r);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "lastResults/{checkId}/{filter}")
    public ResponseEntity<CheckChartResult> getLastResults(@PathVariable(value = "checkId") String checkId, @PathVariable(value = "filter") String filter, @RequestParam(value = "limit", defaultValue = "1") int limit) {
        CheckChartResult cr = service.getFilteredLastResults(checkId, filter, limit);
        return new ResponseEntity<>(cr, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "alertCoverage", method = RequestMethod.POST)
    public ResponseEntity<JsonNode> getAlertCoverage(@RequestBody JsonNode filter) {
        JsonNode node = service.getAlertCoverage(filter);
        if (null == node) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "entity-filters", method = RequestMethod.POST)
    public ResponseEntity<EntityFilterResponse> getMatchingEntities(@RequestBody EntityFilterRequest filter) {
        EntityFilterResponse response = service.getEntitiesMatchingFilters(filter);
        if (null == response) {
            return new ResponseEntity<>(new EntityFilterResponse("Exception encountered in filtering the entities. Please try again or validate the entity included/excluded filter"), HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value="/search", method = RequestMethod.GET)
    public ResponseEntity<SearchAPI.QuickSearchResult> search(@RequestParam(name = "query", defaultValue = "") String search, @RequestParam(required = false, name = "teams") String teams, @RequestParam(name = "limit", defaultValue = "25") int limit) {
        return searchAPI.search(search, teams, limit, null);
    }
}

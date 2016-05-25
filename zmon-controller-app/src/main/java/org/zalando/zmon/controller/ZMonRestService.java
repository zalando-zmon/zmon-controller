package org.zalando.zmon.controller;

import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.kairosdb.client.KairosDBOAuthHttpClient;
import org.kairosdb.client.builder.AggregatorFactory;
import org.kairosdb.client.builder.DataFormatException;
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.QueryMetric;
import org.kairosdb.client.builder.TimeUnit;
import org.kairosdb.client.builder.grouper.TagGrouper;
import org.kairosdb.client.response.Queries;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.Results;
import org.kairosdb.client.response.grouping.TagGroupResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.KairosDBProperties;
import org.zalando.zmon.config.MetricCacheProperties;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.CheckHistoryGroupResult;
import org.zalando.zmon.domain.CheckHistoryResult;
import org.zalando.zmon.domain.CheckResults;
import org.zalando.zmon.domain.ExecutionStatus;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.rest.EntityApi;
import org.zalando.zmon.rest.domain.CheckChartResult;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.ZMonService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/rest")
public class ZMonRestService extends AbstractZMonController {

    private final Logger log = LoggerFactory.getLogger(ZMonRestService.class);

    @Autowired
    private ZMonService service;

    @Autowired
    private KairosDBProperties kairosDBProperties;

    @Autowired
    private MetricCacheProperties metricCacheProperties;

    @Autowired
    private EntityApi entityApi;

    @Autowired
    DefaultZMonPermissionService authService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    AccessTokens accessTokens;

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<ExecutionStatus> getStatus() {
        return new ResponseEntity<>(service.getStatus(), HttpStatus.OK);
    }

    @RequestMapping(value = "/allTeams", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getAllTeams() {
        return new ResponseEntity<>(service.getAllTeams(), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkDefinitions", method = RequestMethod.GET)
    public ResponseEntity<List<CheckDefinition>> getAllCheckDefinitions(
            @RequestParam(value = "team", required = false) final Set<String> teams) {
        final List<CheckDefinition> defs = teams == null ? service.getCheckDefinitions(null).getCheckDefinitions()
                : service.getCheckDefinitions(null, teams);

        return new ResponseEntity<>(defs, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateCheckDefinition")
    public ResponseEntity<CheckDefinition> updateCheckDefinition(@RequestBody(required = true) CheckDefinitionImport check) {
        if (check.getOwningTeam() == null || check.getOwningTeam().equals("")) {
            if (authService.getTeams().isEmpty()) {
                check.setOwningTeam("ZMON");
            } else {
                check.setOwningTeam((String) authService.getTeams().toArray()[0]);
            }
        }

        check.setLastModifiedBy(authService.getUserName());
        CheckDefinition saved = service.createOrUpdateCheckDefinition(check);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @RequestMapping(value = "/checkDefinition", method = RequestMethod.GET)
    public ResponseEntity<CheckDefinition> getCheckDefinition(
            @RequestParam(value = "check_id", required = true) final int checkId) {

        final List<CheckDefinition> checkDefinitions = service.getCheckDefinitions(null, Lists.newArrayList(checkId));
        if (checkDefinitions.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(checkDefinitions.get(0), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkResults", method = RequestMethod.GET)
    public ResponseEntity<List<CheckResults>> getCheckResults(
            @RequestParam(value = "check_id", required = true) final int checkId,
            @RequestParam(value = "entity", required = false) final String entity,
            @RequestParam(value = "limit", defaultValue = "20") final int limit) {

        return new ResponseEntity<>(service.getCheckResults(checkId, entity, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "checkResultsChart", method = RequestMethod.GET)
    public ResponseEntity<CheckChartResult> getChartResults(
            @RequestParam(value = "check_id", required = true) final int checkId,
            @RequestParam(value = "entity", required = false) final String entity,
            @RequestParam(value = "limit", defaultValue = "20") final int limit) {
        return new ResponseEntity<>(service.getChartResults(checkId, entity, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "/checkAlertResults", method = RequestMethod.GET)
    public ResponseEntity<List<CheckResults>> getCheckAlertResults(
            @RequestParam(value = "alert_id", required = true) final int alertId,
            @RequestParam(value = "limit", defaultValue = "20") final int limit) {
        return new ResponseEntity<>(service.getCheckAlertResults(alertId, limit), HttpStatus.OK);
    }

    @RequestMapping(value = "/entityProperties", method = RequestMethod.GET)
    public ResponseEntity<JsonNode> getEntityProperties() {
        return new ResponseEntity<>(service.getEntityProperties(), HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "/entities")
    public void getEntities(@RequestParam(value = "query", defaultValue = "{}") String data, final Writer writer,
                            final HttpServletResponse response) {
        entityApi.getEntities(data, writer, response);
    }


    @ResponseBody
    @RequestMapping(value = "cloud-view-endpoints", method = RequestMethod.GET, produces = "application/json")
    public void cloudViewEndpoints(@RequestParam(value = "application_id") String applicationId, final Writer writer,
                                   final HttpServletResponse response) throws IOException {

        final Executor executor = Executor.newInstance();

        final String dataServiceQuery = metricCacheProperties.getUrl() + "/api/v1/rest-api-metrics/kairosdb-format?application_id=" + applicationId;

        int nodeId = Math.abs(applicationId.hashCode() % metricCacheProperties.getNodes());
        final String r = executor.execute(Request.Get(dataServiceQuery).addHeader("Cookie", "metric_cache=" + nodeId)).returnContent().asString();

        response.setContentType("application/json");
        writer.write(r);
    }


    @RequestMapping(value = "/retrieveCheckStatistics", method = RequestMethod.GET)
    public ResponseEntity<CheckHistoryResult> retrieveCheckStatistics(
            @RequestParam(value = "check_id", required = true) final int checkId,
            @RequestParam(value = "entity_id", required = true) final String entityId,
            @RequestParam(value = "days", required = false, defaultValue = "0") final int days,
            @RequestParam(value = "hours", required = false, defaultValue = "0") final int hours,
            @RequestParam(value = "days_end", required = false, defaultValue = "0") final int daysEnd,
            @RequestParam(value = "hours_end", required = false, defaultValue = "0") final int hoursEnd,
            @RequestParam(value = "aggregate", required = false, defaultValue = "30") final int aggregate,
            @RequestParam(value = "aggregate_unit", required = false, defaultValue = "minutes") final String aggregateUnit,
            @RequestParam(value = "start_date", required = false) final Long startDate,
            @RequestParam(value = "end_date", required = false) final Long endDate) throws URISyntaxException,
            IOException {

        if (!kairosDBProperties.isEnabled()) {
            return new ResponseEntity<>(new CheckHistoryResult(), HttpStatus.METHOD_NOT_ALLOWED);
        }

        final QueryBuilder builder = QueryBuilder.getInstance();

        if (hours != 0) {
            builder.setStart(hours, TimeUnit.HOURS);
        } else if (days != 0) {
            builder.setStart(days, TimeUnit.DAYS);
        } else if (null != startDate) {
            builder.setStart(new Date(startDate));
        } else {
            builder.setStart(1, TimeUnit.DAYS);
        }

        if (hoursEnd != 0) {
            builder.setEnd(hoursEnd, TimeUnit.HOURS);
        } else if (daysEnd != 0) {
            builder.setEnd(daysEnd, TimeUnit.DAYS);
        } else if (null != endDate) {
            builder.setEnd(new Date(endDate));
        }

        final QueryMetric metric = builder.addMetric("zmon.check." + checkId)
                .addTag("entity", entityId.replace(":", "_").replace("[", "_").replace("]", "_").replace("@", "_")).addGrouper(
                        new TagGrouper("key"));

        if ("hours".equals(aggregateUnit)) {
            metric.addAggregator(AggregatorFactory.createAverageAggregator(aggregate, TimeUnit.HOURS));
        } else if ("days".equals(aggregateUnit)) {
            metric.addAggregator(AggregatorFactory.createAverageAggregator(aggregate, TimeUnit.DAYS));
        } else {
            metric.addAggregator(AggregatorFactory.createAverageAggregator(aggregate, TimeUnit.MINUTES));
        }

        String kairosDBBaseUrl = null;
        for(KairosDBProperties.KairosDBServiceConfig c : kairosDBProperties.getKairosdbs()) {
            if (c.getName().equals("kairosdb") || kairosDBProperties.getKairosdbs().size() == 1) {
                kairosDBBaseUrl = c.getUrl();
            }
        }

        if (null == kairosDBBaseUrl) {
            log.error("No KairosDB configured for Check Charts");
            return new ResponseEntity<>(new CheckHistoryResult(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final KairosDBOAuthHttpClient client = new KairosDBOAuthHttpClient(kairosDBBaseUrl, kairosDBProperties.getHttpClient(), accessTokens.get(KairosDBController.KAIROSDB_TOKEN_ID));

        try {
            final Long queryStart = System.currentTimeMillis();
            final QueryResponse response = client.query(builder);
            final Long queryEnd = System.currentTimeMillis();

            // TODO: consider changing the log level to DEBUG here, not sure why we need INFO..
            log.info("Querying KairosDB for check/entity {}/{} in {}ms aggregate: {} {} range: {} - {}", checkId,
                    entityId, queryEnd - queryStart, aggregate, aggregateUnit,
                    builder.getStartAbsolute() != null ? builder.getStartAbsolute() : builder.getStartRelative(),
                    builder.getEndAbsolute() != null ? builder.getEndAbsolute() : builder.getEndRelative());

            final CheckHistoryResult r = new CheckHistoryResult();
            r.entityId = entityId;
            r.name = "zmon.check." + checkId;

            for (final Queries qs : response.getQueries()) {
                for (final Results rs : qs.getResults()) {
                    final CheckHistoryGroupResult groupResult = new CheckHistoryGroupResult();

                    if (null == rs.getGroupResults()) {

                        // no values returned from kairosdb for this entity/check
                        continue;
                    }

                    final TagGroupResult tagGroup = (TagGroupResult) rs.getGroupResults().get(0);
                    groupResult.key = tagGroup.getGroup().get("key");

                    r.groupResults.add(groupResult);

                    for (final DataPoint dp : rs.getDataPoints()) {
                        final List<JsonNode> l = new ArrayList<>();

                        try {
                            if (dp.isIntegerValue()) {
                                l.add(new LongNode(dp.getTimestamp()));
                                l.add(new LongNode(dp.longValue()));
                            } else {
                                l.add(new LongNode(dp.getTimestamp()));
                                l.add(new DoubleNode(dp.doubleValue()));
                            }
                        } catch (DataFormatException dfe) {

                        }

                        groupResult.values.add(l);
                    }
                }
            }

            return new ResponseEntity<>(r, HttpStatus.OK);
        } finally {
            client.shutdown();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "lastResults/{checkId}/{filter}", method = RequestMethod.GET)
    public ResponseEntity<CheckChartResult> getLastResults(@PathVariable(value = "checkId") String checkId, @PathVariable(value = "filter") String filter, @RequestParam(value = "limit", defaultValue = "1") int limit) throws ZMonException {
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
}

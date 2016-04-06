package org.zalando.zmon.controller;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.*;
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
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.config.KairosDBProperties;
import org.zalando.zmon.config.MetricCacheProperties;
import org.zalando.zmon.domain.*;
import org.zalando.zmon.exception.ZMonException;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;
import org.zalando.zmon.rest.EntityApi;
import org.zalando.zmon.rest.domain.CheckChartResult;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.ZMonService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/rest/kairosDBPost")
public class KairosDBController extends AbstractZMonController {

    @Autowired
    private KairosDBProperties kairosDBProperties;

    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    DefaultZMonPermissionService authService;

    @Autowired
    ObjectMapper mapper;

    /* For Grafana2 KairosDB plugin we need to prepend the original KairosDB URLs too */

    @ResponseBody
    @RequestMapping(value = {"","/api/v1/datapoints/query"}, method = RequestMethod.POST, produces = "application/json")
    public void kairosDBPost(@RequestBody(required = true) final JsonNode node, final Writer writer,
                             final HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        if (!kairosDBProperties.isEnabled()) {
            writer.write("");
            return;
        }

        String checkId = node.get("metrics").get(0).get("name").textValue().replace("zmon\\.check\\.", "");
        Timer.Context timer = metricRegistry.timer("kairosdb.check.query."+checkId).time();

        // align all queries to full minutes
        if (node instanceof ObjectNode) {
            ObjectNode q = (ObjectNode) node;
            q.put("cache_time", 60);
            if (q.has("start_absolute")) {
                long start = q.get("start_absolute").asLong();
                start = start - (start % 60000);
                q.put("start_absolute", start);
            }
        }

        final Executor executor = Executor.newInstance();

        final String kairosDBURL = kairosDBProperties.getUrl() + "/api/v1/datapoints/query";

        final String r = executor.execute(Request.Post(kairosDBURL).addHeader("X-ZMON-CHECK-ID", checkId).useExpectContinue().bodyString(node.toString(),
                ContentType.APPLICATION_JSON)).returnContent().asString();

        if(timer!=null) {
            timer.stop();
        }

        writer.write(r);
    }

    @ResponseBody
    @RequestMapping(value = {"/tags", "/api/v1/datapoints/query/tags"} , method = RequestMethod.POST, produces = "application/json")
    public void kairosDBtags(@RequestBody(required = true) final JsonNode node, final Writer writer,
                             final HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        if (!kairosDBProperties.isEnabled()) {
            writer.write("");
            return;
        }

        final Executor executor = Executor.newInstance();

        final String kairosDBURL = kairosDBProperties.getUrl() + "/api/v1/datapoints/query/tags";

        final String r = executor.execute(Request.Post(kairosDBURL).useExpectContinue().bodyString(node.toString(),
                ContentType.APPLICATION_JSON)).returnContent().asString();

        writer.write(r);
    }

    @ResponseBody
    @RequestMapping(value = {"/metrics", "/api/v1/metricnames"}, method = RequestMethod.GET, produces = "application/json")
    public void kairosDBmetrics(final Writer writer, final HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        if (!kairosDBProperties.isEnabled()) {
            writer.write("");
            return;
        }

        final String kairosDBURL = kairosDBProperties.getUrl() + "/api/v1/metricnames";

        final String r = Request.Get(kairosDBURL).useExpectContinue().execute().returnContent().asString();

        writer.write(r);
    }
}

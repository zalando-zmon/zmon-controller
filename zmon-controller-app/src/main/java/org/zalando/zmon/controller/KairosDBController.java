package org.zalando.zmon.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.KairosDBProperties;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * NOTE: we inject the AUTHORIZATION-HEADER manually. Better would be to use an
 * Interceptor. But this is coming with Spring 4.3,<br/>
 * {@link https://jira.spring.io/browse/SPR-12538}
 *
 * @author jbellmann
 */
@RestController
@RequestMapping(value = "/rest/kairosDBPost")
public class KairosDBController extends AbstractZMonController {

    public static final String KAIROSDB_TOKEN_ID = "kairosdb";

    private static final String BEARER = "Bearer ";

    private final MetricRegistry metricRegistry;

    private final AsyncRestTemplate asyncRestTemplate;

    private final String metricNamesKairosDBURL;

    private final String tagsKairosDBURL;

    private final String queryKairosDBURL;

    private final AccessTokens accessTokens;

    @Autowired
    public KairosDBController(KairosDBProperties kairosDBProperties, MetricRegistry metricRegistry,
                              AsyncRestTemplate asyncRestTemplate, AccessTokens accessTokens) {
        this.metricRegistry = metricRegistry;
        this.asyncRestTemplate = asyncRestTemplate;
        this.accessTokens = accessTokens;
        if (kairosDBProperties.isEnabled()) {
            metricNamesKairosDBURL = kairosDBProperties.getKairosdbs().get(0).getUrl() + "/api/v1/metricnames";
            tagsKairosDBURL = kairosDBProperties.getKairosdbs().get(0).getUrl() + "/api/v1/datapoints/query/tags";
            queryKairosDBURL = kairosDBProperties.getKairosdbs().get(0).getUrl() + "/api/v1/datapoints/query";
        } else {
            metricNamesKairosDBURL = "";
            tagsKairosDBURL = "";
            queryKairosDBURL = "";
        }
    }

    /*
     * For Grafana2 KairosDB plugin we need to prepend the original KairosDB
     * URLs too
     */
    @RequestMapping(value = {"",
            "/api/v1/datapoints/query"}, method = RequestMethod.POST, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBPost(@RequestBody(required = true) final JsonNode node) {
        final String checkId = node.get("metrics").get(0).get("name").textValue().replace("zmon.check.", "");
        Timer.Context timer = metricRegistry.timer("kairosdb.check.query." + checkId).time();

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-ZMON-CHECK-ID", checkId);
        headers.add(AUTHORIZATION, BEARER + accessTokens.get(KAIROSDB_TOKEN_ID));

        HttpEntity<String> httpEntity = new HttpEntity<>(node.toString(), headers);

        ListenableFuture<ResponseEntity<JsonNode>> lf = asyncRestTemplate.exchange(queryKairosDBURL, HttpMethod.POST,
                httpEntity, JsonNode.class);
        lf.addCallback(new StopTimerCallback(timer));

        return lf;
    }

    @RequestMapping(value = {"/tags",
            "/api/v1/datapoints/query/tags"}, method = RequestMethod.POST, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBtags(@RequestBody(required = true) final JsonNode node) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTHORIZATION, BEARER + accessTokens.get(KAIROSDB_TOKEN_ID));

        HttpEntity<String> httpEntity = new HttpEntity<>(node.toString(), headers);

        return asyncRestTemplate.exchange(tagsKairosDBURL, HttpMethod.POST, httpEntity, JsonNode.class);
    }

    @RequestMapping(value = {"/metrics",
            "/api/v1/metricnames"}, method = RequestMethod.GET, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBmetrics() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTHORIZATION, BEARER + accessTokens.get(KAIROSDB_TOKEN_ID));

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        return asyncRestTemplate.exchange(metricNamesKairosDBURL, HttpMethod.GET, httpEntity, JsonNode.class);
    }

    static class StopTimerCallback implements ListenableFutureCallback<Object> {

        private final Timer.Context timer;

        StopTimerCallback(Timer.Context timer) {
            this.timer = timer;
        }

        @Override
        public void onSuccess(Object result) {
            closeTimer();
        }

        @Override
        public void onFailure(Throwable ex) {
            closeTimer();
        }

        protected void closeTimer() {
            if (timer != null) {
                timer.stop();
            }
        }
    }
}

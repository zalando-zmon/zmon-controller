package org.zalando.zmon.controller;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.AsyncRestTemplate;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.KairosDBProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.zalando.zmon.config.KairosDBProperties.KairosDBServiceConfig;

/**
 * NOTE: we inject the AUTHORIZATION-HEADER manually. Better would be to use an
 * Interceptor. But this is coming with Spring 4.3,<br/>
 * @see <a href="https://jira.spring.io/browse/SPR-12538">issue</a>
 *
 * @author jbellmann
 */
@RestController
@RequestMapping(value = "/rest/kairosdbs/")
public class MultiKairosDBController extends AbstractZMonController {

    private static final String KAIROSDB_TOKEN_ID = "kairosdb";
    private static final String TAGS_QUERY_SUFFIX = "/api/v1/datapoints/query/tags";
    private static final String QUERY_SUFFIX = "/api/v1/datapoints/query";
    private static final String METRIC_NAMES_SUFFIX = "/api/v1/metricnames";
    private static final String BEARER = "Bearer ";
    private final Logger log = LoggerFactory.getLogger(MultiKairosDBController.class);
    private final MetricRegistry metricRegistry;

    private final AsyncRestTemplate asyncRestTemplate;

    private final AccessTokens accessTokens;

    private final Map<String, KairosDBServiceConfig> kairosdbServices = new HashMap<>();

    @Autowired
    public MultiKairosDBController(final KairosDBProperties kairosDBProperties,
                                   final MetricRegistry metricRegistry,
                                   final AsyncRestTemplate asyncRestTemplate,
                                   final AccessTokens accessTokens) {
        this.metricRegistry = metricRegistry;
        this.asyncRestTemplate = asyncRestTemplate;
        this.accessTokens = accessTokens;

        for (KairosDBServiceConfig c : kairosDBProperties.getKairosdbs()) {
            kairosdbServices.put(c.getName(), c);
            log.info("Registering: name={} url={} oauth={} timewindow={}", c.getName(), c.getUrl(), c.isOauth2(), c.getMaxWindowLength());
        }
    }

    /*
     * For Grafana2 KairosDB plugin we need to prepend the original KairosDB
     * URLs too
     */
    @RequestMapping(
            value = "{kairosdbId}/api/v1/datapoints/query",
            method = RequestMethod.POST,
            produces = "application/json"
    )
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBPost(@RequestBody final JsonNode node,
                                                                   @PathVariable(value = "kairosdbId") String kairosDB) {
        if (!kairosdbServices.containsKey(kairosDB)) {
            return null;
        }
        final KairosDBServiceConfig kairosProperties = kairosdbServices.get(kairosDB);

        final String checkId = getCheckId(node);
        final Timer.Context timer = metricRegistry.timer("kairosdb.check.query." + checkId).time();

        fixMetricNames(node);
        final int queryWindow = kairosProperties.getMaxWindowLength();
        alignQueriesToMinutes(node, queryWindow);

        final HttpHeaders headers = setUpHeaders(kairosProperties);
        headers.add("X-ZMON-CHECK-ID", checkId);
        final HttpEntity<String> httpEntity = new HttpEntity<>(node.toString(), headers);

        final String url = kairosProperties.getUrl() + QUERY_SUFFIX;
        final ListenableFuture<ResponseEntity<JsonNode>> lf =
                asyncRestTemplate.exchange(url, HttpMethod.POST,httpEntity, JsonNode.class);
        lf.addCallback(new StopTimerCallback(timer));
        return lf;
    }

    @RequestMapping(value = "{kairosdbId}/api/v1/datapoints/query/tags", method = RequestMethod.POST, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBtags(@RequestBody final JsonNode node,
                                                                   @PathVariable(value = "kairosdbId") String kairosDB) {
        if (!kairosdbServices.containsKey(kairosDB)) {
            return null;
        }
        final KairosDBServiceConfig kairosProperties = kairosdbServices.get(kairosDB);
        final HttpEntity<String> httpEntity = new HttpEntity<>(setUpHeaders(kairosProperties));

        final String url = kairosProperties.getUrl() + TAGS_QUERY_SUFFIX;
        return asyncRestTemplate.exchange(url, HttpMethod.POST, httpEntity, JsonNode.class);
    }

    @RequestMapping(value = "{kairosdbId}/api/v1/metricnames", method = RequestMethod.GET, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBmetrics(@PathVariable(value = "kairosdbId") String kairosDB) {
        if (!kairosdbServices.containsKey(kairosDB)) {
            return null;
        }
        final KairosDBServiceConfig kairosProperties = kairosdbServices.get(kairosDB);
        final HttpEntity<String> httpEntity = new HttpEntity<>(setUpHeaders(kairosProperties));

        final String url = kairosProperties.getUrl() + METRIC_NAMES_SUFFIX;
        return asyncRestTemplate.exchange(url, HttpMethod.GET, httpEntity, JsonNode.class);
    }

    private String getCheckId(final JsonNode node) {
        final JsonNode metrics = node.get("metrics");
        if (metrics.size() > 0) {
            final Optional<JsonNode> nameNode = Optional.ofNullable(metrics.get(0).get("nameNode"));
            final Optional<String> checkId = nameNode.map(n -> n.textValue().replace("zmon.check.", ""));
            return checkId.orElse("");
        }
        return "";
    }

    private void fixMetricNames(final JsonNode node) {
        for (final JsonNode metric : node.get("metrics")) {
            final JsonNode tags = metric.get("tags");
            final Optional<JsonNode> keyNode = Optional.ofNullable(tags.get("key"));
            if (keyNode.isPresent()) {
                final String prefix = metric.get("name").textValue();
                final String suffix = keyNode.get().textValue();
                final String metricName = prefix + "." + suffix;
                ((ObjectNode) metric).put("name", metricName);
                ((ObjectNode) tags).remove("key");
            }
        }
    }

    private HttpHeaders setUpHeaders(final KairosDBServiceConfig kairosDbProps) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (kairosDbProps.isOauth2()) {
            headers.add(AUTHORIZATION, BEARER + accessTokens.get(KAIROSDB_TOKEN_ID));
        }
        return headers;
    }

    private void alignQueriesToMinutes(final JsonNode node, final int queryWindow) {
        if (node.isObject()) {
            ObjectNode q = (ObjectNode) node;
            q.put("cache_time", 60);
            if (q.has("start_absolute")) {
                long start = q.get("start_absolute").asLong();
                start = start - (start % 60000);
                q.put("start_absolute", start);
            } else if (q.has("start_relative")) {
                if (queryWindow != 0) {
                    ObjectNode r = (ObjectNode) q.get("start_relative");
                    r.put("value", queryWindow);
                    r.put("unit", "minutes");
                }
            }
        }
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

        void closeTimer() {
            if (timer != null) {
                timer.stop();
            }
        }
    }
}

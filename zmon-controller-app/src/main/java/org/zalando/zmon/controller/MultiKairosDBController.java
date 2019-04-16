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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * NOTE: we inject the AUTHORIZATION-HEADER manually. Better would be to use an
 * Interceptor. But this is coming with Spring 4.3,<br/>
 * {@see <a href="https://jira.spring.io/browse/SPR-12538">docs</a>}
 *
 * @author jbellmann
 */
@RestController
@RequestMapping(value = "/rest/kairosdbs/")
public class MultiKairosDBController extends AbstractZMonController {

    private final Logger log = LoggerFactory.getLogger(MultiKairosDBController.class);

    public static final String KAIROSDB_TOKEN_ID = "kairosdb";

    private static final String TAGS_QUERY_SUFFIX = "/api/v1/datapoints/query/tags";

    private static final String QUERY_SUFFIX = "/api/v1/datapoints/query";

    private static final String METRIC_NAMES_SUFFIX = "/api/v1/metricnames";

    private static final String BEARER = "Bearer ";

    private final MetricRegistry metricRegistry;

    private final AsyncRestTemplate asyncRestTemplate;

    private final AccessTokens accessTokens;

    private final Map<String, KairosDBProperties.KairosDBServiceConfig> kairosdbServices = new HashMap<>();

    @Autowired
    public MultiKairosDBController(KairosDBProperties kairosDBProperties, MetricRegistry metricRegistry,
                                   AsyncRestTemplate asyncRestTemplate, AccessTokens accessTokens) {
        this.metricRegistry = metricRegistry;
        this.asyncRestTemplate = asyncRestTemplate;
        this.accessTokens = accessTokens;

        for (KairosDBProperties.KairosDBServiceConfig c : kairosDBProperties.getKairosdbs()) {
            kairosdbServices.put(c.getName(), c);
            log.info("Registering: name={} url={} oauth={} timewindow={}", c.getName(), c.getUrl(), c.isOauth2(), c.getMaxWindowLength());
        }
    }

    /*
     * For Grafana2 KairosDB plugin we need to prepend the original KairosDB
     * URLs too
     */
    @RequestMapping(value = "{kairosdbId}/api/v1/datapoints/query", method = RequestMethod.POST, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBPost(@RequestBody final JsonNode node,
                                                                   @PathVariable(value = "kairosdbId") final String kairosDB,
                                                                   final HttpServletRequest req) {
        if (!kairosdbServices.containsKey(kairosDB)) {
            return null;
        }

        KairosDBProperties.KairosDBServiceConfig kairosProperties = kairosdbServices.get(kairosDB);

        final String checkId = node.get("metrics").get(0).get("name").textValue().replace("zmon.check.", "");
        Timer.Context timer = metricRegistry.timer("kairosdb.check.query." + checkId).time();

        alignQueriesToFullMinutes(node, kairosProperties.getMaxWindowLength());

        HttpHeaders headers = prepareHeaders(kairosDB);
        headers.add("X-ZMON-CHECK-ID", checkId);

        Optional.ofNullable(req.getHeader("Referer")).ifPresent(referer -> {
            final String[] parts = referer.split("/");
            if (parts.length > 1) {
                final String dashboard = parts[parts.length - 1];
                log.info("datapoints_query checkId=" + checkId + " dashboard=" + dashboard);
            }
        });

        HttpEntity<String> httpEntity = new HttpEntity<>(node.toString(), headers);

        ListenableFuture<ResponseEntity<JsonNode>> lf = asyncRestTemplate.exchange(
                kairosdbServices.get(kairosDB).getUrl() + QUERY_SUFFIX, HttpMethod.POST, httpEntity, JsonNode.class);
        lf.addCallback(new StopTimerCallback(timer));
        return lf;
    }

    @RequestMapping(value = "{kairosdbId}/api/v1/datapoints/query/tags", method = RequestMethod.POST, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBtags(@RequestBody final JsonNode node,
                                                                   @PathVariable(value = "kairosdbId") String kairosDB) {
        if (!kairosdbServices.containsKey(kairosDB)) {
            return null;
        }

        HttpHeaders headers = prepareHeaders(kairosDB);

        HttpEntity<String> httpEntity = new HttpEntity<>(node.toString(), headers);

        return asyncRestTemplate.exchange(kairosdbServices.get(kairosDB).getUrl() + TAGS_QUERY_SUFFIX, HttpMethod.POST, httpEntity, JsonNode.class);
    }

    @RequestMapping(value = "{kairosdbId}/api/v1/metricnames", method = RequestMethod.GET, produces = "application/json")
    public ListenableFuture<ResponseEntity<JsonNode>> kairosDBmetrics(@PathVariable(value = "kairosdbId") String kairosDB) {
        if (!kairosdbServices.containsKey(kairosDB)) {
            return null;
        }

        HttpHeaders headers = prepareHeaders(kairosDB);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        return asyncRestTemplate.exchange(kairosdbServices.get(kairosDB).getUrl() + METRIC_NAMES_SUFFIX, HttpMethod.GET, httpEntity, JsonNode.class);
    }

    private HttpHeaders prepareHeaders(final String kairosDB) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (kairosdbServices.get(kairosDB).isOauth2()) {
            headers.add(AUTHORIZATION, BEARER + accessTokens.get(KAIROSDB_TOKEN_ID));
        }
        return headers;
    }

    private void alignQueriesToFullMinutes(final JsonNode node, final int queryWindow) {
        if (node instanceof ObjectNode) {
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

        protected void closeTimer() {
            if (timer != null) {
                timer.stop();
            }
        }
    }
}

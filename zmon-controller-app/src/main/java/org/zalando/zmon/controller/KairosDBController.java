package org.zalando.zmon.controller;

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
import org.zalando.zmon.config.KairosDBProperties;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping(value = "/rest/kairosDBPost")
public class KairosDBController extends AbstractZMonController {

    // private final KairosDBProperties kairosDBProperties;

    private final MetricRegistry metricRegistry;

    // private final Executor executor;

    private final AsyncRestTemplate asyncRestTemplate;

    private final String metricNamesKairosDBURL;

    private final String tagsKairosDBURL;

    private final String queryKairosDBURL;

    @Autowired
    public KairosDBController(
            KairosDBProperties kairosDBProperties,
            MetricRegistry metricRegistry, AsyncRestTemplate asyncRestTemplate
    ) {
        // this.kairosDBProperties = kairosDBProperties;
        this.metricRegistry = metricRegistry;
        this.asyncRestTemplate = asyncRestTemplate;
        if (kairosDBProperties.isEnabled()) {
            metricNamesKairosDBURL = kairosDBProperties.getUrl() + "/api/v1/metricnames";
            tagsKairosDBURL = kairosDBProperties.getUrl() + "/api/v1/datapoints/query/tags";
            queryKairosDBURL = kairosDBProperties.getUrl() + "/api/v1/datapoints/query";
        } else {
            metricNamesKairosDBURL = "";
            tagsKairosDBURL = "";
            queryKairosDBURL = "";
        }
        // executor = Executor.newInstance(kairosDBProperties.getHttpClient());
    }


    /* For Grafana2 KairosDB plugin we need to prepend the original KairosDB URLs too */
    @RequestMapping(value = {"", "/api/v1/datapoints/query"}, method = RequestMethod.POST, produces = "application/json")
    public ListenableFuture<ResponseEntity<String>> kairosDBPost(@RequestBody(required = true) final JsonNode node) {

        // if (!kairosDBProperties.isEnabled()) {
        // writer.write("");
        // return;
        // }

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
        HttpEntity<String> httpEntity = new HttpEntity<>(node.toString(), headers);

        ListenableFuture<ResponseEntity<String>> lf = asyncRestTemplate.exchange(queryKairosDBURL, HttpMethod.POST,
                httpEntity, String.class);

        lf.addCallback(new ListenableFutureCallback<Object>() {

            @Override
            public void onSuccess(Object result) {
                if (timer != null) {
                    timer.stop();
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                if (timer != null) {
                    timer.stop();
                }
            }
        });

        return lf;
        // final String r =
        // executor.execute(Request.Post(queryKairosDBURL).addHeader("X-ZMON-CHECK-ID",
        // checkId).useExpectContinue().bodyString(node.toString(),
        // ContentType.APPLICATION_JSON)).returnContent().asString();

        // if (timer != null) {
        // timer.stop();
        // }

    }

    @RequestMapping(value = {"/tags", "/api/v1/datapoints/query/tags"}, method = RequestMethod.POST, produces = "application/json")
    public ListenableFuture<ResponseEntity<String>> kairosDBtags(@RequestBody(required = true) final JsonNode node) {
        //
        // if (!kairosDBProperties.isEnabled()) {
        // writer.write("");
        // return;
        // }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(node.toString(), headers);
        return asyncRestTemplate.exchange(tagsKairosDBURL, HttpMethod.POST, httpEntity, String.class);

        // final String r =
        // executor.execute(Request.Post(tagsKairosDBURL).useExpectContinue().bodyString(node.toString(),
        // ContentType.APPLICATION_JSON)).returnContent().asString();
        //
        // writer.write(r);
    }

    @RequestMapping(value = {"/metrics", "/api/v1/metricnames"}, method = RequestMethod.GET, produces = "application/json")
    public ListenableFuture<ResponseEntity<String>> kairosDBmetrics() {

        // if (!kairosDBProperties.isEnabled()) {
        // writer.write("");
        // return;
        // }
        return asyncRestTemplate.getForEntity(metricNamesKairosDBURL, String.class);

        // final String r =
        // executor.execute(Request.Get(kairosDBURL).useExpectContinue()).returnContent().asString();
        //
        // writer.write(r);
    }
}

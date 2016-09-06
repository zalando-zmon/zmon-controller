package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.domain.Alert;
import org.zalando.zmon.domain.CheckResults;
import org.zalando.zmon.domain.ExecutionStatus;
import org.zalando.zmon.redis.ResponseHolder;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.service.ZMonService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

/**
 * Created by jmussler on 11/17/14.
 */
@Controller
@RequestMapping("/api/v1/status")
public class AlertStatusAPI {

    private final ZMonService service;
    private final JedisPool jedisPool;
    protected ObjectMapper mapper;

    private final AlertService alertService;

    @Autowired
    public AlertStatusAPI(final ZMonService service, final AlertService alertService, final JedisPool p, final ObjectMapper m) {
        this.service = service;
        this.alertService = alertService;
        jedisPool = p;
        mapper = m;
    }

    /**
     * General system status (also used by ZMON CLI)
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<ExecutionStatus> getStatus() {
        return new ResponseEntity<>(service.getStatus(), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/alert/{id}/all-entities")
    public ResponseEntity<List<CheckResults>> getAlertStatus(@PathVariable("id") final int id) throws IOException {
        return new ResponseEntity<>(service.getCheckAlertResults(id, 1), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = {"/alert/{ids}/", "/alert/{ids}"})
    public JsonNode getAlertStatus(@PathVariable("ids") final List<String> ids) throws IOException {
        Map<String, List<ResponseHolder<String, String>>> results = ids.stream()
                .collect(Collectors.toMap(identity(), id -> new ArrayList<>()));

        try (Jedis jedis = jedisPool.getResource()) {
            List<ResponseHolder<String, Set<String>>> responses;

            try (Pipeline p = jedis.pipelined()) {
                responses = ids.stream()
                        .map(id -> ResponseHolder.create(id, p.smembers("zmon:alerts:" + id)))
                        .collect(Collectors.toList());
            }

            try (Pipeline p = jedis.pipelined()) {
                for (ResponseHolder<String, Set<String>> r : responses) {
                    for (String entityId : r.getResponse().get()) {
                        results.get(r.getKey()).add(ResponseHolder.create(entityId, p.get("zmon:alerts:" + r.getKey() + ":" + entityId)));
                    }
                }
            }
        }

        ObjectNode resultNode = mapper.createObjectNode();

        for (String id : ids) {
            List<ResponseHolder<String, String>> lr = results.get(id);
            if (lr.size() > 0) {
                ObjectNode entities = mapper.createObjectNode();
                for (ResponseHolder<String, String> rh : lr) {
                    String alertDetails = rh.getResponse().get();
                    // alert details might not be set due to race condition
                    if (alertDetails != null) {
                        entities.set(rh.getKey(), mapper.readTree(alertDetails));
                    }
                }
                resultNode.set(id, entities);
            }
        }

        return resultNode;
    }

    @ResponseBody
    @RequestMapping(value = "/alert-coverage", method = RequestMethod.POST)
    public ResponseEntity<JsonNode> getAlertCoverage(@RequestBody JsonNode filter) {
        JsonNode node = service.getAlertCoverage(filter);
        if (null == node) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @RequestMapping(value = "/alert/{alert_id}/details")
    public ResponseEntity<Alert> getAlert(@PathVariable(value = "alert_id") final Integer alertId) {
        final Alert alert = alertService.getAlert(alertId);

        return alert == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(alert, HttpStatus.OK);
    }

    @RequestMapping(value = "/active-alerts", method = RequestMethod.GET)
    public ResponseEntity<List<Alert>> getAllAlerts(
            @RequestParam(value = "team", required = false) final Set<String> teams,
            @RequestParam(value = "tags", required = false) final Set<String> tags) {
        final List<Alert> alerts = teams == null && tags == null ? alertService.getAllAlerts()
                : alertService.getAllAlertsByTeamAndTag(teams, tags);

        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }
}

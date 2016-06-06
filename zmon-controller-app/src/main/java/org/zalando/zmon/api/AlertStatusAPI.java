package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.domain.CheckResults;
import org.zalando.zmon.domain.ExecutionStatus;
import org.zalando.zmon.redis.ResponseHolder;
import org.zalando.zmon.service.ZMonService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.*;

/**
 * Created by jmussler on 11/17/14.
 */
@Controller
@RequestMapping("/api/v1/status")
public class AlertStatusAPI {

    private ZMonService service;
    private final JedisPool jedisPool;
    protected ObjectMapper mapper;

    private static final Logger LOG = LoggerFactory.getLogger(AlertStatusAPI.class);

    @Autowired
    public AlertStatusAPI(final ZMonService service, final JedisPool p, final ObjectMapper m) {
        this.service = service;
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
    @RequestMapping(value = "/alert/{id}/all-entities", method = RequestMethod.GET)
    public ResponseEntity<List<CheckResults>> getAlertStatus(@PathVariable("id") final int id) throws IOException {
        return new ResponseEntity<>(service.getCheckAlertResults(id, 1), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/alert/{ids}/", method = RequestMethod.GET)
    public JsonNode getAlertStatus(@PathVariable("ids") final List<String> ids) throws IOException {
        Jedis jedis = jedisPool.getResource();

        Map<String, List<ResponseHolder<String, String>>> results = new HashMap<>();
        for (String id : ids) {
            results.put(id, new ArrayList<>());
        }

        try {
            List<ResponseHolder<String, Set<String>>> responses = new ArrayList<>();
            {
                Pipeline p = jedis.pipelined();
                for (String id : ids) {
                    responses.add(ResponseHolder.create(id, p.smembers("zmon:alerts:" + id)));
                }
                p.sync();
            }

            {
                Pipeline p = jedis.pipelined();
                for (ResponseHolder<String, Set<String>> r : responses) {
                    for (String m : r.getResponse().get()) {
                        results.get(r.getKey()).add(ResponseHolder.create(m, p.get("zmon:alerts:" + r.getKey() + ":" + m)));
                    }
                }
                p.sync();
            }
        } finally {
            jedis.close();
        }

        ObjectNode resultNode = mapper.createObjectNode();

        for (String id : ids) {
            List<ResponseHolder<String, String>> lr = results.get(id);
            ObjectNode entities = mapper.createObjectNode();
            for (ResponseHolder<String, String> rh : lr) {
                entities.put(rh.getKey(), mapper.readTree(rh.getResponse().get()));
            }
            if (lr.size() > 0) {
                resultNode.put(id, entities);
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
}

package org.zalando.zmon.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author raparida
 */
public interface FalsePositiveRateService {

    ResponseEntity<JsonNode> getFalsePositiveRate(String id);

    ResponseEntity<JsonNode> getFalsePositiveRateDataPoints(String id, Map<String, String> query);

    ResponseEntity<JsonNode> listFalsePositiveRates(String[] idList);
}

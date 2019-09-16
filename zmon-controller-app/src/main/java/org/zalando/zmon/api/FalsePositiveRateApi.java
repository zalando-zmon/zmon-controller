package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.zalando.zmon.service.FalsePositiveRateService;

import java.util.Map;

/**
 * @author raparida
 */

@Controller
@RequestMapping(value = "/api/v1/false-positive-rates")
public class FalsePositiveRateApi {

    private final FalsePositiveRateService falsePositiveRateService;
    private final Logger log = LoggerFactory.getLogger(FalsePositiveRateApi.class);

    @Autowired
    public FalsePositiveRateApi(FalsePositiveRateService falsePositiveRateService) {
        this.falsePositiveRateService = falsePositiveRateService;
    }

    @ResponseBody
    @GetMapping(value = "/{id}")
    public ResponseEntity<JsonNode> getFalsePositiveRate(@PathVariable(value = "id") String id) {
        return falsePositiveRateService.getFalsePositiveRate(id);
    }

    @ResponseBody
    @GetMapping(value = "/{id}/datapoints")
    public ResponseEntity<JsonNode> getFalsePositiveRateDataPoints(
            @PathVariable(value = "id") String id,
            @RequestParam Map<String, String> query) {
        return falsePositiveRateService.getFalsePositiveRateDataPoints(id, query);
    }

    @ResponseBody
    @GetMapping(value = "")
    public ResponseEntity<JsonNode> listFalsePositiveRates(@RequestParam(name = "id", required = true) String[] idList) {
        return falsePositiveRateService.listFalsePositiveRates(idList);
    }
}

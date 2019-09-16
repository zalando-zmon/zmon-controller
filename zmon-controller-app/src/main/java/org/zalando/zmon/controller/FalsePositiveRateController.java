package org.zalando.zmon.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
@RequestMapping(value = "/rest/false-positive-rates")
public class FalsePositiveRateController {
    private final FalsePositiveRateService falsePositiveRateService;

    @Autowired
    public FalsePositiveRateController(FalsePositiveRateService falsePositiveRateService) {
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
    public ResponseEntity<JsonNode> listFalsePositiveRates(@RequestParam(name = "id") String[] idList) {
        return falsePositiveRateService.listFalsePositiveRates(idList);
    }
}
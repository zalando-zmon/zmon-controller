package org.zalando.zmon.domain;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmussler on 7/2/15.
 */

public class CheckChartResult {
    public final Map<String, List<JsonNode>> values = new HashMap<>();
}

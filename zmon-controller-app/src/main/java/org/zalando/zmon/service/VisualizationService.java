package org.zalando.zmon.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface VisualizationService {
    String homeRedirect();

    String dynamicDashboardRedirect(Map<String, String> params);

    ResponseEntity<JsonNode> getDashboard(String id, String token);

    ResponseEntity<JsonNode> searchDashboards(Map<String, String> params, String token);

    ResponseEntity<JsonNode> upsertDashboard(String dashboard, String token);

    ResponseEntity<JsonNode> deleteDashboard(String id, String token);
}

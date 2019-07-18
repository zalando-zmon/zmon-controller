package org.zalando.zmon.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface VisualizationService {
    String homeRedirect();

    String dynamicDashboardRedirect(Map<String, String> params);

    ResponseEntity<JsonNode> getDashboard(String id);

    ResponseEntity<JsonNode> searchDashboards(String query, int limit);

    ResponseEntity<JsonNode> upsertDashboard(String dashboard);

    ResponseEntity<JsonNode> deleteDashboard(String id);
}

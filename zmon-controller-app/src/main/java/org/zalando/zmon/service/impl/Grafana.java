package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.zmon.config.VisualizationProperties;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.VisualizationService;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

@Service
public class Grafana implements VisualizationService {

    private final Logger log = LoggerFactory.getLogger(Grafana.class);

    private final String upsertDashboardEndpoint = "/api/dashboards/db";
    private final String dynamicDashboardEndpoint = "/dashboard/script/zmon-check.js";
    private final String getDashboardByUidEndpoint = "/api/dashboards/uid/";
    private final String deleteDashboardByUidEndpoint = "/api/dashboards/uid/";
    private final String searchDashboardEndpoint = "/api/search/";

    @Autowired
    private VisualizationProperties visualizationProperties;

    @Autowired
    private DefaultZMonPermissionService authService;

    @Autowired
    protected ObjectMapper mapper;

    @Override
    public String homeRedirect() {
        return "redirect:" + visualizationProperties.getUrl();
    }

    @Override
    public String dynamicDashboardRedirect(Map<String, String> params) {
        UriComponents uri = UriComponentsBuilder.newInstance()
                .host(visualizationProperties.getUrl())
                .path(dynamicDashboardEndpoint)
                .queryParam("orgId", "1")
                .queryParam("checkId", params.containsKey("checkId") ? params.get("checkId") : "")
                .queryParam("entityName", params.containsKey("entityName") ? params.get("entityName") : "")
                .queryParam("checkName", params.containsKey("checkName") ? params.get("checkName") : "")
                .queryParam("refresh", "1m")
                .build();

        return "redirect:" + uri.toUriString();
    }

    @Override
    public ResponseEntity<JsonNode> getDashboard(String uid) {
        final Executor executor = Executor.newInstance(visualizationProperties.getHttpClient());
        final String url = visualizationProperties.getUrl() + getDashboardByUidEndpoint + uid;

        try {
            Request request = Request.Get(url);
            request.addHeader("Authorization", "Bearer " + authService.getUserName());
            HttpResponse response = executor.execute(request).returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Get grafana dashboard: {} failed", uid, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> searchDashboards(String query, int limit) {
        log.info("Searching grafana dashboard: Query={} User={}", query, authService.getUserName());
        final Executor executor = Executor.newInstance(visualizationProperties.getHttpClient());

        try {
            UriComponents url = UriComponentsBuilder.newInstance()
                    .host(visualizationProperties.getUrl())
                    .path(searchDashboardEndpoint)
                    .queryParam("query", URLEncoder.encode(query, "UTF-8"))
                    .queryParam("limit", limit)
                    .build();
            Request request = Request.Get(url.toUri());
            request.addHeader("Authorization", "Bearer " + authService.getUserName());
            HttpResponse response = executor.execute(request).returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Search grafana dashboard failed", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> upsertDashboard(String dashboard) {
        log.info("Creating/Updating grafana dashboard: user={}", authService.getUserName());
        final Executor executor = Executor.newInstance(visualizationProperties.getHttpClient());
        final String url = visualizationProperties.getUrl() + upsertDashboardEndpoint;

        try {
            Request request = Request.Post(url);
            request.addHeader("Authorization", "Bearer " + authService.getUserName());
            HttpResponse response = executor.execute(request.bodyString(
                    mapper.writeValueAsString(dashboard), ContentType.APPLICATION_JSON))
                    .returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Create/Update grafana dashboard failed", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> deleteDashboard(String uid) {
        log.info("Deleting grafana dashboard: uid={} user={}", uid, authService.getUserName());

        final Executor executor = Executor.newInstance(visualizationProperties.getHttpClient());
        final String url = visualizationProperties.getUrl() + deleteDashboardByUidEndpoint + uid;

        try {
            Request request = Request.Delete(url);
            request.addHeader("Authorization", "Bearer " + authService.getUserName());
            HttpResponse response = executor.execute(request).returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Delete grafana dashboard: {} failed", uid, ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<JsonNode> toResponseEntity(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
            HttpEntity entity = response.getEntity();
            String dashboard = EntityUtils.toString(entity);
            JsonNode node = mapper.readTree(dashboard);
            return new ResponseEntity<>(node, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.valueOf(status));
    }
}

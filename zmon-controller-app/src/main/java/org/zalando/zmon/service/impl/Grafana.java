package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
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
import java.util.Set;

@Service
public class Grafana implements VisualizationService {

    private final Logger log = LoggerFactory.getLogger(Grafana.class);

    private final String upsertDashboardEndpoint = "/api/dashboards/db";
    private final String dynamicDashboardEndpoint = "/dashboard/script/zmon-check.js";
    private final String getAndDeleteDashboardEndPoint = "/api/dashboards/uid/";
    private final String searchDashboardEndpoint = "/api/search";

    private final String grafanaUpgradeHint = "- Hints: 1.Use latest Grafana6 dashboard format. " +
            "2.Upgrade your ZMON cli";

    private final Executor executor;
    private VisualizationProperties visualizationProperties;
    private DefaultZMonPermissionService authService;
    protected ObjectMapper mapper;

    /* Allowed status code 412 - Reason being a possible status code for upsert-dashboard in Grafana is 412 with
        reason of the failure inside the body:
        https://grafana.com/docs/http_api/dashboard/#create-update-dashboard */
    private final Set<Integer> allowedStatusCode = ImmutableSet.of(200, 412);

    @Autowired
    public Grafana(VisualizationProperties visualizationProperties,
                   DefaultZMonPermissionService authService,
                   ObjectMapper mapper) {
        this.visualizationProperties = visualizationProperties;
        this.authService = authService;
        this.mapper = mapper;
        this.executor = Executor.newInstance(visualizationProperties.getHttpClient());
    }

    @Override
    public String homeRedirect() {
        return "redirect:" + visualizationProperties.getUrl();
    }

    @Override
    public String dynamicDashboardRedirect(Map<String, String> params) {
        UriComponents uri = UriComponentsBuilder.fromUriString(visualizationProperties.getUrl())
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
    public ResponseEntity<JsonNode> getDashboard(String uid, String token) {
        final String url = visualizationProperties.getUrl() + getAndDeleteDashboardEndPoint + uid;

        try {
            Request request = Request.Get(url);
            request.addHeader("Authorization", "Bearer " + token);
            HttpResponse response = executor.execute(request).returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Get grafana dashboard: {} failed", uid, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> searchDashboards(Map<String, String> params, String token) {
        try {
            log.info("Searching grafana dashboard: Query={} User={}",
                    params.containsKey("query") ? URLEncoder.encode(params.get("query"), "UTF-8") : "",
                    authService.getUserName());
            UriComponents url = UriComponentsBuilder.fromUriString(visualizationProperties.getUrl())
                    .path(searchDashboardEndpoint)
                    .queryParam("query", params.containsKey("query") ? URLEncoder.encode(params.get("query"), "UTF-8") : "")
                    .queryParam("tag", params.containsKey("tag") ? URLEncoder.encode(params.get("tag"), "UTF-8") : "")
                    .queryParam("limit", params.containsKey("limit") ? params.get("limit") : "25")
                    .build();
            Request request = Request.Get(url.toUri());
            request.addHeader("Authorization", "Bearer " + token);
            log.info("Making grafana search - {}", url.toUri());

            HttpResponse response = executor.execute(request).returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Search grafana dashboard failed", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> upsertDashboard(String dashboard, String token) {
        log.info("Creating/Updating grafana dashboard: user={}", authService.getUserName());
        final String url = visualizationProperties.getUrl() + upsertDashboardEndpoint;

        try {
            Request request = Request.Post(url);
            request.addHeader("Authorization", "Bearer " + token);
            HttpResponse response = executor.execute(request.bodyString(
                    dashboard, ContentType.APPLICATION_JSON))
                    .returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Create/Update grafana dashboard failed", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> deleteDashboard(String uid, String token) {
        log.info("Deleting grafana dashboard: uid={} user={}", uid, authService.getUserName());
        final String url = visualizationProperties.getUrl() + getAndDeleteDashboardEndPoint + uid;

        try {
            Request request = Request.Delete(url);
            request.addHeader("Authorization", "Bearer " + token);
            HttpResponse response = executor.execute(request).returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Delete grafana dashboard: {} failed", uid, ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<JsonNode> toResponseEntity(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();

        if (allowedStatusCode.contains(status) && entity != null) {
            String dashboard = EntityUtils.toString(entity);
            JsonNode node = mapper.readTree(dashboard);

            // Possible reason of 412 can be client is using a old Grafana dashboard JSON format
            if (status == 412 && node.hasNonNull("message")) {
                ((ObjectNode) node).put("message",
                        node.get("message").textValue() + grafanaUpgradeHint);
            }

            return new ResponseEntity<>(node, HttpStatus.valueOf(status));
        }
        return new ResponseEntity<>(HttpStatus.valueOf(status));
    }
}

package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.ResponseEntity;
import org.zalando.zmon.config.VisualizationProperties;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author raparida
 */
@RunWith(MockitoJUnitRunner.class)
public class GrafanaTest {
    private static final int MOCK_SERVER_PORT = 9000;
    private static final String VISUALIZATION_URL = "http://localhost:" + MOCK_SERVER_PORT;
    private static final String VALID_TOKEN = "abc";
    private static final String INVALID_TOKEN = "xyz";
    private static final String VALID_JSON = "{\"dashboard\":\"1\"}";
    private static final String INVALID_JSON = "{\"dashboard\":\"1\"";
    private static final String PRECONDITION_MESSAGE = "{\"message\":\"Test message\"}";

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, MOCK_SERVER_PORT);
    private MockServerClient mockServerClient;

    @Mock
    private DefaultZMonPermissionService authorityService;
    @Mock
    private VisualizationProperties visualizationProperties;
    @Mock
    private ObjectMapper mockMapper;
    @InjectMocks
    private Grafana grafana;

    @Before
    public void setup() throws Exception {
        when(visualizationProperties.getUrl()).thenReturn(VISUALIZATION_URL);
        when(mockMapper.readTree(VALID_JSON)).thenReturn(new ObjectMapper().readTree(VALID_JSON));
        when(mockMapper.readTree(PRECONDITION_MESSAGE)).thenReturn(new ObjectMapper().readTree(PRECONDITION_MESSAGE));
        // Get and Delete dashboard
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/uid/1")
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(VALID_JSON));
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/uid/2")
                        .withHeader("Authorization", "Bearer " + INVALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(401));
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/uid/3")
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(403));
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/uid/4")
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(404));

        // Upsert dashboard
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/db")
                        .withMethod("POST")
                        .withBody(VALID_JSON)
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(200));
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/db")
                        .withMethod("POST")
                        .withBody(INVALID_JSON)
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(400));
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/db")
                        .withMethod("POST")
                        .withBody(VALID_JSON)
                        .withHeader("Authorization", "Bearer " + INVALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(401));
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/db")
                        .withMethod("POST")
                        .withBody("{\"dashboard\":\"2\"}")
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(403));
        mockServerClient.when(
                HttpRequest.request("/api/dashboards/db")
                        .withMethod("POST")
                        .withBody("{\"dashboard\":\"3\"}")
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(412)
                        .withBody("{\"message\":\"Test message\"}"));

        // Search dashboard
        mockServerClient.when(
                HttpRequest.request("/api/search")
                        .withMethod("GET")
                        .withQueryStringParameter("query", "test")
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withBody(VALID_JSON));
        mockServerClient.when(
                HttpRequest.request("/api/search")
                        .withMethod("GET")
                        .withQueryStringParameter("query", "notfound")
                        .withHeader("Authorization", "Bearer " + VALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(400));
        mockServerClient.when(
                HttpRequest.request("/api/search")
                        .withMethod("GET")
                        .withQueryStringParameter("query", "test")
                        .withHeader("Authorization", "Bearer " + INVALID_TOKEN))
                .respond(HttpResponse.response().withStatusCode(401));
    }

    @Test
    public void testHomeRedirect() throws Exception {
        assertThat(grafana.homeRedirect(), equalTo("redirect:" + VISUALIZATION_URL));
    }

    @Test
    public void testDynamicDashboardRedirect() throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("orgId", "1");
        param.put("checkId", "123");
        param.put("entityName", "entity1");
        param.put("checkName", "check123");

        String response = "redirect:" + VISUALIZATION_URL + "/dashboard/script/zmon-check.js?"
                + "orgId=" + param.get("orgId") + "&"
                + "checkId=" + param.get("checkId") + "&"
                + "entityName=" + param.get("entityName") + "&"
                + "checkName=" + param.get("checkName") + "&"
                + "refresh=1m";
        assertThat(grafana.dynamicDashboardRedirect(param), equalTo(response));
    }

    @Test
    public void testGetDashboard() throws Exception {
        ResponseEntity<JsonNode> response = grafana.getDashboard("1", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        assertThat(response.getBody().toString(), equalTo(VALID_JSON));

        response = grafana.getDashboard("2", INVALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(401));

        response = grafana.getDashboard("3", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(403));

        response = grafana.getDashboard("4", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(404));
    }

    @Test
    public void testDeleteDashboard() throws Exception {
        ResponseEntity<JsonNode> response = grafana.deleteDashboard("1", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        assertThat(response.getBody().toString(), equalTo(VALID_JSON));

        response = grafana.deleteDashboard("2", INVALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(401));

        response = grafana.deleteDashboard("3", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(403));

        response = grafana.deleteDashboard("4", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(404));
    }

    @Test
    public void testUpsertDashboard() throws Exception {
        ResponseEntity<JsonNode> response = grafana.upsertDashboard(VALID_JSON, VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(200));

        response = grafana.upsertDashboard(INVALID_JSON, VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(400));

        response = grafana.upsertDashboard(VALID_JSON, INVALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(401));

        response = grafana.upsertDashboard("{\"dashboard\":\"2\"}", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(403));

        response = grafana.upsertDashboard("{\"dashboard\":\"3\"}", VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(412));
        assertThat(response.getBody().toString(), equalTo("{\"message\":\"Test message\"}"));
    }

    @Test
    public void testSearchDashboard() throws Exception {
        Map<String, String> validQuery = new HashMap<>();
        validQuery.put("query", "test");
        validQuery.put("limit", "1");
        ResponseEntity<JsonNode> response = grafana.searchDashboards(validQuery, VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        assertThat(response.getBody().toString(), equalTo(VALID_JSON));

        response = grafana.searchDashboards(validQuery, INVALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(401));

        Map<String, String> invalidQuery = new HashMap<>();
        invalidQuery.put("query", "notfound");
        invalidQuery.put("limit", "1");
        response = grafana.searchDashboards(invalidQuery, VALID_TOKEN);
        assertThat(response.getStatusCodeValue(), equalTo(400));
    }
}

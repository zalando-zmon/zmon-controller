package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.MetaDataProperties;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

/**
 * @author raparida
 */

@RunWith(MockitoJUnitRunner.class)
public class FalsePositiveRateServiceImplTest {
    private static final int MOCK_SERVER_PORT = 9000;
    private static final String METADATA_SERVICE_URL = "http://localhost:" + MOCK_SERVER_PORT;
    private static final String FALSE_POSITIVE_RATE_END_POINT = "/api/false-positive-rates/";

    private static final String VALID_FALSE_POSITIVE_RATE = "{\"alertId\":\"123\"}";

    private static final String VALID_DATA_POINTS = "{\"id\":123,\"uri\":\"/false-positive-rates/2026\"," +
            "\"values\":[[124,40.5]]}";
    private static final String EMPTY_DATA_POINTS = "{\"id\":999,\"uri\":\"/false-positive-rates/2026\"," +
            "\"values\":[]}";

    private static final String VALID_TOKEN = "abc";

    private static final String VALID_ALERT_ID = "123";
    private static final String NOT_FOUND_ALERT_ID = "999";

    private ClientAndServer mockServer;

    @Mock
    private MetaDataProperties metaDataProperties;
    @Mock
    private AccessTokens accessTokens;
    @Mock
    private ObjectMapper mockMapper;
    @InjectMocks
    private FalsePositiveRateServiceImpl falsePositiveRateService;

    @Before
    public void setup() throws Exception {
        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        MockServerClient client = new MockServerClient("127.0.0.1", MOCK_SERVER_PORT);

        when(metaDataProperties.getUrl()).thenReturn(METADATA_SERVICE_URL);
        when(accessTokens.get("zmon")).thenReturn(VALID_TOKEN);
        when(mockMapper.readTree(VALID_FALSE_POSITIVE_RATE)).thenReturn(new ObjectMapper().readTree(VALID_FALSE_POSITIVE_RATE));
        when(mockMapper.readTree(VALID_DATA_POINTS)).thenReturn(new ObjectMapper().readTree(VALID_DATA_POINTS));
        when(mockMapper.readTree(EMPTY_DATA_POINTS)).thenReturn(new ObjectMapper().readTree(EMPTY_DATA_POINTS));

        // Get false positive rate
        client.when(
                HttpRequest.request()
                        .withPath(FALSE_POSITIVE_RATE_END_POINT + VALID_ALERT_ID))
                .respond(
                        HttpResponse.response()
                                .withBody(VALID_FALSE_POSITIVE_RATE)
                                .withStatusCode(HttpStatus.OK.value()));

        client.when(
                HttpRequest.request()
                        .withPath(FALSE_POSITIVE_RATE_END_POINT + NOT_FOUND_ALERT_ID))
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.NOT_FOUND.value()));

        // Get false positive rate data-points
        client.when(
                HttpRequest.request(FALSE_POSITIVE_RATE_END_POINT + VALID_ALERT_ID + "/datapoints")
                        .withMethod("GET")
                        .withQueryStringParameter("from", "123")
                        .withQueryStringParameter("to", "999"))
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody(VALID_DATA_POINTS));
        client.when(
                HttpRequest.request(FALSE_POSITIVE_RATE_END_POINT + NOT_FOUND_ALERT_ID + "/datapoints")
                        .withMethod("GET")
                        .withQueryStringParameter("from", "123")
                        .withQueryStringParameter("to", "999"))
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.OK.value())
                                .withBody(EMPTY_DATA_POINTS));
        client.when(
                HttpRequest.request(FALSE_POSITIVE_RATE_END_POINT + VALID_ALERT_ID + "/datapoints")
                        .withMethod("GET"))
                .respond(
                        HttpResponse.response()
                                .withStatusCode(HttpStatus.BAD_REQUEST.value()));

    }

    @After
    public void stopServer() {
        mockServer.stop();
    }

    @Test
    public void testGetFalsePositiveRate() throws Exception {
        ResponseEntity<JsonNode> response = falsePositiveRateService.getFalsePositiveRate(VALID_ALERT_ID);
        assertThat(response.getStatusCodeValue(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getBody().toString(), equalTo(VALID_FALSE_POSITIVE_RATE));

        response = falsePositiveRateService.getFalsePositiveRate(NOT_FOUND_ALERT_ID);
        assertThat(response.getStatusCodeValue(), equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void testGetFalsePositiveRateDataPoints() throws Exception {
        Map<String, String> validQuery = new HashMap<>();
        validQuery.put("from", "123");
        validQuery.put("to", "999");
        ResponseEntity<JsonNode> response = falsePositiveRateService.
                getFalsePositiveRateDataPoints(VALID_ALERT_ID, validQuery);
        assertThat(response.getStatusCodeValue(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getBody().toString(), equalTo(VALID_DATA_POINTS));

        response = falsePositiveRateService.getFalsePositiveRateDataPoints(NOT_FOUND_ALERT_ID, validQuery);
        assertThat(response.getStatusCodeValue(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getBody().toString(), equalTo(EMPTY_DATA_POINTS));

        Map<String, String> invalidQuery = new HashMap<>();
        invalidQuery.put("invalid", "query");
        response = falsePositiveRateService.getFalsePositiveRateDataPoints(VALID_ALERT_ID, invalidQuery);
        assertThat(response.getStatusCodeValue(), equalTo(HttpStatus.BAD_REQUEST.value()));
    }
}

package org.zalando.zmon.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.MetaDataProperties;
import org.zalando.zmon.service.FalsePositiveRateService;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author raparida
 */
@Service
public class FalsePositiveRateServiceImpl implements FalsePositiveRateService {
    private final Logger log = LoggerFactory.getLogger(FalsePositiveRateServiceImpl.class);
    private final Executor executor;
    private final MetaDataProperties metaDataProperties;
    protected ObjectMapper mapper;
    private final AccessTokens accessTokens;
    private static final String FALSE_POSITIVE_RATE_END_POINT = "/api/false-positive-rates/";
    private static final String BEARER = "Bearer ";
    private static final String ZMON_TOKEN_ID = "zmon";

    /* Allowed status code 207 - Reason being a possible status code for bulk operations in MetadataService is 207
    with individual errors inside the response body:
    https://opensource.zalando.com/restful-api-guidelines/#152 */
    private static final Set<Integer> ALLOWED_STATUS_CODES = ImmutableSet.of(200, 207);

    @Autowired
    public FalsePositiveRateServiceImpl(MetaDataProperties metaDataProperties,
                                        AccessTokens accessTokens,
                                        ObjectMapper mapper) {
        this.metaDataProperties = metaDataProperties;
        this.mapper = mapper;
        this.executor = Executor.newInstance(metaDataProperties.getHttpClient());
        this.accessTokens = accessTokens;
    }

    @Override
    public ResponseEntity<JsonNode> getFalsePositiveRate(final String alertId) {
        final String url = metaDataProperties.getUrl() + FALSE_POSITIVE_RATE_END_POINT + alertId;
        try {
            Request request = Request.Get(url);

            log.debug("False positive alert id: {}", alertId);
            log.debug("URL: {}", url);

            request.addHeader(AUTHORIZATION, BEARER + accessTokens.get(ZMON_TOKEN_ID));
            HttpResponse response = executor.execute(request).returnResponse();
            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Get false positive rate for Alert: {} failed", alertId, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> getFalsePositiveRateDataPoints(final String alertId, final Map<String, String> query) {
        try {
            String from = query.containsKey("from") ? URLEncoder.encode(query.get("from"), "UTF-8") : "";
            String to = query.containsKey("to") ? URLEncoder.encode(query.get("to"), "UTF-8") : "";

            log.info("Searching false-positive rate data points for Alert: {} From={} To={}", alertId, from, to);

            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(metaDataProperties.getUrl())
                    .path(FALSE_POSITIVE_RATE_END_POINT + alertId + "/datapoints")
                    .queryParam("from", from)
                    .queryParam("to", to);

            log.debug("URL: {}", urlBuilder.build().toUri().toString());

            Request request = Request.Get(urlBuilder.build().toUri());
            request.addHeader(AUTHORIZATION, BEARER + accessTokens.get(ZMON_TOKEN_ID));
            HttpResponse response = executor.execute(request).returnResponse();

            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Get false positive rate for Alert: {} failed", alertId, ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<JsonNode> listFalsePositiveRates(final String[] idList) {
        log.debug("Bulk get false positive rates");
        final String url = metaDataProperties.getUrl() + FALSE_POSITIVE_RATE_END_POINT;
        try {
            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("id", idList);
            log.debug("URL: {}", url);

            Request request = Request.Get(urlBuilder.build().toUri());

            request.addHeader(AUTHORIZATION, BEARER + accessTokens.get(ZMON_TOKEN_ID));
            HttpResponse response = executor.execute(request).returnResponse();

            return toResponseEntity(response);
        } catch (Exception ex) {
            log.error("Bulk get of false positive rate failed", ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<JsonNode> toResponseEntity(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();

        if (ALLOWED_STATUS_CODES.contains(status) && entity != null) {
            String resp = EntityUtils.toString(entity);
            JsonNode node = mapper.readTree(resp);
            return new ResponseEntity<>(node, HttpStatus.valueOf(status));
        }
        return new ResponseEntity<>(HttpStatus.valueOf(status));
    }
}

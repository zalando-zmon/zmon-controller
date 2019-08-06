package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.zmon.config.RestConfiguration;
import org.zalando.zmon.service.ZMonService;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlertStatusAPITest {

    @Mock
    private ZMonService service;

    private AlertStatusAPI api;

    @Before
    public void setUp() {
        when(service.getAlertResults(any())).thenReturn(Collections.emptyList());

        api = new AlertStatusAPI(service, null, null, new ObjectMapper(), new RestConfiguration());
    }

    @Test
    public void getAlertResultsShouldReturnBadRequestIfFiltersAreNull() {
        ResponseEntity response = api.getAlertResults(null);

        MatcherAssert.assertThat(response.getStatusCode(), Matchers.is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAlertResultsShouldReturnBadRequestIfNotFiltersSet() {
        ResponseEntity response = api.getAlertResults("{}");

        MatcherAssert.assertThat(response.getStatusCode(), Matchers.is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAlertResultsShouldReturnBadRequestIfFilterIsNotAValidJson() {
        ResponseEntity response = api.getAlertResults("}{");

        MatcherAssert.assertThat(response.getStatusCode(), Matchers.is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAlertResultsShouldReturnBadRequestIfNoFilterHasANonEmptyValue() {
        ResponseEntity response = api.getAlertResults("{\"application\":\"\"}");

        MatcherAssert.assertThat(response.getStatusCode(), Matchers.is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAlertResultsShouldReturnBadRequestIfFilterKeyIsNotAllowed() {
        ResponseEntity response = api.getAlertResults("{\"not-allowed-filter-key\":\"\"}");

        MatcherAssert.assertThat(response.getStatusCode(), Matchers.is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void getAlertResultsShouldReturnAlertResults() {
        ResponseEntity response = api.getAlertResults("{\"application\":\"api-repository\"}");

        MatcherAssert.assertThat(response.getStatusCode(), Matchers.is(HttpStatus.OK));
    }
}

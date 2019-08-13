package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.service.ZMonService;
import redis.clients.jedis.JedisPool;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AlertStatusAPI.class},
    webEnvironment = MOCK,
    //yaml and json it's a list
    properties = {"zmon.alert-results.allowed-filters=a,b"})
@Ignore("doesn't work: if you want to try it locally, add the following to the environment variables: SPRING_APPLICATION_JSON={\"zmon\": { \"alert-results\": {\"allowed-filters\": [\"c\",\"d\"]}}}")
public class AllowedFiltersConfigTest {

    @MockBean
    private ZMonService zMonService;

    @MockBean
    private AlertService alertService;

    @MockBean
    private JedisPool jedisPool;

    @MockBean
    private ObjectMapper objectMapper;

    @Autowired
    private AlertStatusAPI alertStatusAPI;

    @Test
    public void testAllowedFilters() {
        Assertions.assertThat(alertStatusAPI.getAlertResultsConfig().getAllowedFilters()).containsExactly("c", "d");
    }
}

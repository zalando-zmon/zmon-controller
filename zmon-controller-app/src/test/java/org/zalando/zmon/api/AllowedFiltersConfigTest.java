package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.zmon.service.AlertService;
import org.zalando.zmon.service.ZMonService;
import redis.clients.jedis.JedisPool;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AlertStatusAPI.class})
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

    @BeforeClass
    public static void setUp() throws Exception {
        System.setProperty("spring.application.json", "{\"zmon\": { \"alert-results\": {\"allowed-filters\": [\"c\",\"d\"]}}}");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        System.clearProperty("spring.application.json");
    }

    @Test
    public void testAllowedFilters() {
        Assertions.assertThat(alertStatusAPI.getAlertResultsConfig().getAllowedFilters()).containsExactly("c", "d");
    }
}

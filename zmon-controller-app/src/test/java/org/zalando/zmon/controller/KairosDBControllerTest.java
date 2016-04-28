package org.zalando.zmon.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.AsyncRestTemplate;
import org.zalando.zmon.config.KairosDBProperties;

import com.codahale.metrics.MetricRegistry;

public class KairosDBControllerTest {

    private MockMvc mockMvc;
    private MetricRegistry metricsRegistry;

    @Before
    public void setUp() throws MalformedURLException {
        this.metricsRegistry = Mockito.mock(MetricRegistry.class);

        KairosDBProperties properties = new KairosDBProperties();
        properties.setEnabled(true);
        properties.setUrl(new URL("http://localhost:9998"));
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new KairosDBController(properties, metricsRegistry, new AsyncRestTemplate()))
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    public void testKairosDbMetrics() throws Exception {
        mockMvc.perform(get("/rest/kairosDBPost/api/v1/metricnames"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testKairosDbTags() throws Exception {
        mockMvc.perform(post("/rest/kairosDBPost/api/v1/datapoints/query/tags")
                .content("{\"key\":\"value\"}").contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Ignore
    @Test
    public void testKairosDbPost() throws Exception {
        mockMvc.perform(post("/rest/kairosDBPost/api/v1/datapoints/query")
                .content("{\"metrics\":[{\"name\":\"value\"}]}")
                .contentType(APPLICATION_JSON)).andExpect(status().isOk());
    }

}

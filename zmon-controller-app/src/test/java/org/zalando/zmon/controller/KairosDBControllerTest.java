package org.zalando.zmon.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.AsyncRestTemplate;
import org.zalando.zmon.config.KairosDBProperties;

import com.codahale.metrics.MetricRegistry;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class KairosDBControllerTest {

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(9998);

    private MockMvc mockMvc;
    private MetricRegistry metricsRegistry;

    @Before
    public void setUp() throws MalformedURLException {

        wireMockRule.stubFor(post(urlPathEqualTo("/api/v1/datapoints/query/tags"))
                .willReturn(aResponse().withStatus(200).withBody("{}").withFixedDelay(100)));
        wireMockRule.stubFor(post(urlPathEqualTo("/api/v1/metricnames"))
                .willReturn(aResponse().withStatus(200).withBody("{}").withFixedDelay(100)));

        this.metricsRegistry = new MetricRegistry();

        KairosDBProperties properties = new KairosDBProperties();
        properties.setEnabled(true);
        properties.setUrl(new URL("http://localhost:9998"));
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new KairosDBController(properties, metricsRegistry,
                        new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory())))
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

    @Test
    public void testKairosDbPost() throws Exception {
        mockMvc.perform(post("/rest/kairosDBPost/api/v1/datapoints/query")
                .content("{\"metrics\":[{\"name\":\"value\"}]}")
                .contentType(APPLICATION_JSON)).andExpect(status().isOk());
    }

}

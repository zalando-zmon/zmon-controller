package org.zalando.zmon.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.AsyncRestTemplate;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.zmon.config.KairosDBProperties;

import com.codahale.metrics.MetricRegistry;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class MultiKairosDBControllerTest {

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(9998);

    private MockMvc mockMvc;
    private MetricRegistry metricsRegistry;

    @Before
    public void setUp() throws MalformedURLException {

        wireMockRule.stubFor(post(urlPathEqualTo("/api/v1/datapoints/query/tags"))
                .willReturn(aResponse().withStatus(200).withBody("{\"key\":\"value\"}").withHeader("Content-Type", "application/json").withFixedDelay(100)));
        wireMockRule.stubFor(post(urlPathEqualTo("/api/v1/metricnames"))
                .willReturn(aResponse().withStatus(200).withBody("{}").withHeader("Content-Type","appplication/json").withFixedDelay(100)));

        this.metricsRegistry = new MetricRegistry();

        AccessTokens accessTokens = Mockito.mock(AccessTokens.class);
        Mockito.when(accessTokens.get(Mockito.any(String.class))).thenReturn("123456789");
        KairosDBProperties properties = new KairosDBProperties();
        properties.setEnabled(true);
        KairosDBProperties.KairosDBServiceConfig c = new KairosDBProperties.KairosDBServiceConfig();
        c.setName("kairosdb");
        c.setUrl("http://localhost:9998");
        c.setOauth2(false);
        properties.getKairosdbs().add(c);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new MultiKairosDBController(properties, metricsRegistry,
                        new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory()), accessTokens))
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    public void testKairosDbMetrics() throws Exception {
        mockMvc.perform(
                get("/api/kairosdbs/kairosdb/api/v1/metricnames").header(HttpHeaders.AUTHORIZATION, "Bearer 123456789"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testKairosDbTags() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/kairosdbs/kairosdb/api/v1/datapoints/query/tags")
                .header(HttpHeaders.AUTHORIZATION, "Bearer 123456789")
                .content("{\"key\":\"value\"}").contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"key\":\"value\"}"));

        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void testKairosDbPost() throws Exception {
        mockMvc.perform(post("/api/kairosdbs/kairosdb/api/v1/datapoints/query")
                .header(HttpHeaders.AUTHORIZATION, "Bearer 123456789")
                .content("{\"metrics\":[{\"name\":\"value\"}]}")
                .contentType(APPLICATION_JSON)).andExpect(status().isOk());
    }

}

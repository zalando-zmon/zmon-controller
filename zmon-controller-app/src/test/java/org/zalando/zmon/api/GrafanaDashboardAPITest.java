package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.zmon.persistence.GrafanaDashboardSprocService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class GrafanaDashboardAPITest {
    private GrafanaDashboardSprocService grafanaDashboardSprocService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        grafanaDashboardSprocService = Mockito.mock(GrafanaDashboardSprocService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new GrafanaDashboardAPI(grafanaDashboardSprocService, new ObjectMapper(), new DefaultZMonPermissionService()))
                .alwaysDo(MockMvcResultHandlers.print()).build();
    }

    @Test
    public void testSearch() throws Exception {
        GrafanaDashboardSprocService.GrafanaDashboard dashboard = new GrafanaDashboardSprocService.GrafanaDashboard();
        dashboard.title = "mytitle";
        List<GrafanaDashboardSprocService.GrafanaDashboard> results = ImmutableList.of(dashboard);

        when(grafanaDashboardSprocService.getGrafanaDashboards("qqq", null, null, null)).thenReturn(results);
        MvcResult result = mockMvc.perform(post("/api/v1/grafana/_search").header("Content-Type", "application/json").content("{\"query\":{\"query_string\":{\"query\":\"qqq\"}}}")).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringCase("{\"hits\":[{\"_id\":\"mytitle\",\"_type\":\"dashboard\"}]}");
    }
}

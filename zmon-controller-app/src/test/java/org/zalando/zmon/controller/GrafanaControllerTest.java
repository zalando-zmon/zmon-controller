package org.zalando.zmon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.zalando.zmon.domain.CheckResults;
import org.zalando.zmon.service.ZMonService;

import java.io.IOException;

public class GrafanaControllerTest {

    @Test
    public void test() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ZMonService zMonService = Mockito.mock(ZMonService.class);
        CheckResults checkResults = new CheckResults("myentity");
        Mockito.when(zMonService.getCheckResults(123, null, 1)).thenReturn(Lists.newArrayList(checkResults));
        GrafanaController controller = new GrafanaController(zMonService, null, null, mapper, null);
        ResponseEntity<JsonNode> response = controller.serveDynamicDashboard("zmon-check-123");

    }
}

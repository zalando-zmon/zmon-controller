package org.zalando.zmon.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.zmon.exception.ZMonAuthorizationException;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.ZMonService;

import java.util.Collections;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class CheckDefinitionsApiTest {

    private MockMvc mockMvc;
    private ZMonService zMonService;
    private DefaultZMonPermissionService authorityService;


    @Before
    public void setUp() {
        zMonService = Mockito.mock(ZMonService.class);
        authorityService = Mockito.mock(DefaultZMonPermissionService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(new CheckDefinitionsApi(zMonService, authorityService))
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    public void testDeleteUnusedCheckIsForbiddenByPermission() throws Exception {
        Mockito.doThrow(new ZMonAuthorizationException("foo", null, "bar")).when(authorityService).verifyDeleteUnusedCheckDefinitionPermission(100500);
        MvcResult result = mockMvc.perform(delete("/api/v1/check-definitions/100500"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);
    }

    @Test
    public void testDeleteUnusedCheckIsAllowedByPermission() throws Exception {
        when(zMonService.deleteUnusedCheckDef(100500)).thenReturn(Collections.singletonList(100500));
        MvcResult result = mockMvc.perform(delete("/api/v1/check-definitions/100500"))
                .andReturn();


        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }
}

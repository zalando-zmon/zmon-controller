package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.zalando.zmon.persistence.EntitySProcService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class EntityApiTest {

    private EntitySProcService entitySProcService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        entitySProcService = Mockito.mock(EntitySProcService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new EntityApi(entitySProcService, new ObjectMapper(), new DefaultZMonPermissionService()))
                .alwaysDo(MockMvcResultHandlers.print()).build();
    }

    @Test
    public void testAddEntity() throws Exception {
        List<String> teams = ImmutableList.of();
        when(entitySProcService.createOrUpdateEntity("{}", teams, "anonymousUser", false)).thenReturn("myid");
        MvcResult result = mockMvc.perform(post("/api/v1/entities").header("Content-Type", "application/json").content("{}")).andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    public void testAddEntityConstraintViolation() throws Exception {
        List<String> teams = ImmutableList.of();
        when(entitySProcService.createOrUpdateEntity("{}", teams, "anonymousUser", false)).thenThrow(
            new DataIntegrityViolationException("violates check constraint", new PSQLException("", PSQLState.UNKNOWN_STATE)));
        MvcResult result = mockMvc.perform(post("/api/v1/entities").header("Content-Type", "application/json").content("{}")).andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Check constraint violated");
    }
}

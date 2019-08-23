package org.zalando.zmon.checkruntime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.zalando.zmon.controller.domain.CheckRuntimeConfigDto;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.DefinitionRuntime;
import org.zalando.zmon.generator.CheckDefinitionImportGenerator;
import org.zalando.zmon.generator.RandomDataGenerator;
import org.zalando.zmon.service.ZMonService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
abstract class BaseCheckRuntimeIT {
    @Autowired
    protected ZMonService service;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    RandomDataGenerator<CheckDefinitionImport> checkImportGenerator = new CheckDefinitionImportGenerator();
    static final String USER_NAME = "default_user";
    static final List<String> USER_TEAMS = Arrays.asList("Platform/Software","Platform/Monitoring");

    Map<String, Object> getCheckDefinitionFromPublicApiAsMap(int id) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/check-definitions/" + id)
                .header("Authorization", "Bearer testtoken")).andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>(){});
    }

    CheckRuntimeConfigDto getCheckRuntimeConfigWithPrivateApi() throws Exception {
        MvcResult result = mockMvc.perform(get("/rest/checkRuntimeConfig")).andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), CheckRuntimeConfigDto.class);
    }

    DefinitionRuntime getCheckDefinitionRuntimeWithSProc(int id) {
        return service.getCheckDefinitionById(id).map(CheckDefinition::getRuntime).orElse(null);
    }
}

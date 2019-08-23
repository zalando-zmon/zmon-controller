package org.zalando.zmon.checkruntime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

abstract class BaseCheckRuntimeIT {
    @Value("${local.server.port}")
    protected int serverPort;

    @Autowired
    protected ZMonService service;

    @Autowired
    protected ObjectMapper objectMapper;

    Executor executor;

    RandomDataGenerator<CheckDefinitionImport> checkImportGenerator = new CheckDefinitionImportGenerator();
    static final String USER_NAME = "default_user";
    static final List<String> USER_TEAMS = Arrays.asList("Platform/Software","Platform/Monitoring");

    Map<String, Object> getCheckDefinitionFromPublicApiAsMap(int id) throws Exception {
        Request request = Request.Get("http://localhost:" + serverPort + "/api/v1/check-definitions/" + id)
                .setHeader("Authorization", "Bearer testtoken");
        Response response = executor.execute(request);

        return objectMapper.readValue(response.returnContent().asString(), new TypeReference<Map<String, Object>>(){});

    }

    CheckRuntimeConfigDto getCheckRuntimeConfigWithPrivateApi() throws Exception {
        Response response = executor.execute(Request.Get(String.format("http://localhost:%s/rest/checkRuntimeConfig", serverPort)));

        return objectMapper.readValue(response.returnContent().asString(), CheckRuntimeConfigDto.class);
    }

    DefinitionRuntime getCheckDefinitionRuntimeWithSProc(int id) {
        return service.getCheckDefinitionById(id).map(CheckDefinition::getRuntime).orElse(null);
    }


    @Before
    public void setUp() {
        executor = Executor.newInstance();
    }
}

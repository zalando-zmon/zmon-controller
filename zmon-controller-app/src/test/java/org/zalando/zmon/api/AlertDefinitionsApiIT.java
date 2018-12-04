package org.zalando.zmon.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.zalando.zmon.ZmonApplication;
import org.zalando.zmon.config.TestSecurityConfig;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.DefinitionStatus;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        classes = {ZmonApplication.class, TestSecurityConfig.class},
        webEnvironment = RANDOM_PORT,
        properties = {"server.ssl.enabled=false"}
)
public class AlertDefinitionsApiIT {

    private static Integer checkDefinitionId;

    @Rule
    public SpringMethodRule springMethodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private Executor executor;

    private Request create(String resource, String token, String body) {
        return Request.Post("http://localhost:" + port + "/api/v1/" + resource)
                .setHeader("Authorization", "Bearer " + token)
                .bodyString(body, ContentType.APPLICATION_JSON);
    }

    private Request update(String resource, String token, String body) {
        return Request.Put("http://localhost:" + port + "/api/v1/" + resource)
                .setHeader("Authorization", "Bearer " + token)
                .bodyString(body, ContentType.APPLICATION_JSON);
    }

    @Before
    public void setUp() throws Exception {
        this.executor = Executor.newInstance();

        final CheckDefinitionImport check = new CheckDefinitionImport();
        check.setName("test");
        check.setDescription("test");
        check.setInterval(100L);
        check.setCommand("{}");
        check.setEntities(ImmutableList.of(ImmutableMap.of("type", "GLOBAL")));
        check.setOwningTeam("test-team");
        check.setStatus(DefinitionStatus.ACTIVE);
        check.setLastModifiedBy("test-employee");

        final Executor executor = Executor.newInstance();
        final String body = objectMapper.writeValueAsString(check);
        final Response response = executor.execute(create("check-definitions", "test-employee-token", body));

        final String content = response.returnContent().asString();
        final CheckDefinition checkDefinition = objectMapper.readValue(content, CheckDefinition.class);
        checkDefinitionId = checkDefinition.getId();
    }

    @Test
    public void testCreateAlertAsEmployee() throws IOException {
        final AlertDefinition alert = getAlertDefinition("test1");

        final Response response = executor.execute(create("alert-definitions", "test-employee-token", objectMapper.writeValueAsString(alert)));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testUpdateAlertAsEmployee() throws IOException {
        final AlertDefinition alert = getAlertDefinition("test2");

        final Executor executor = Executor.newInstance();
        final Response response = executor.execute(create("alert-definitions", "test-employee-token", objectMapper.writeValueAsString(alert)));
        final String content = response.returnContent().asString();
        final AlertDefinition alertDefinition = objectMapper.readValue(content, AlertDefinition.class);

        alertDefinition.setName("test2-updated");

        final Response response2 = executor.execute(update("alert-definitions/" + alertDefinition.getId(), "test-employee-token", objectMapper.writeValueAsString(alertDefinition)));
        assertThat(response2.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testCreateAlertAsService() throws IOException {
        final AlertDefinition alert = getAlertDefinition("test3");

        final Executor executor = Executor.newInstance();
        final Response response = executor.execute(create("alert-definitions", "test-service-token", objectMapper.writeValueAsString(alert)));

        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testUpdateAlertAsService() throws IOException {
        final AlertDefinition alert = getAlertDefinition("test4");

        final Executor executor = Executor.newInstance();
        final Response response = executor.execute(create("alert-definitions", "test-service-token", objectMapper.writeValueAsString(alert)));

        final String content = response.returnContent().asString();
        final AlertDefinition alertDefinition = objectMapper.readValue(content, AlertDefinition.class);
        alertDefinition.setName("test4-updated");

        final Response response2 = executor.execute(update("alert-definitions/" + alertDefinition.getId(), "test-service-token", objectMapper.writeValueAsString(alertDefinition)));
        assertThat(response2.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testUpdateAlertAsEmployee_failBecauseOfDifferentTeam() throws IOException {
        final AlertDefinition alert = getAlertDefinition("test5");

        final Executor executor = Executor.newInstance();
        final Response response = executor.execute(create("alert-definitions", "test-employee-token", objectMapper.writeValueAsString(alert)));

        final String content = response.returnContent().asString();
        final AlertDefinition alertDefinition = objectMapper.readValue(content, AlertDefinition.class);
        alertDefinition.setName("test5-updated");
        alertDefinition.setTeam("other-team");
        alertDefinition.setResponsibleTeam("other-team");

        final Response response2 = executor.execute(update("alert-definitions/" + alertDefinition.getId(), "test-employee-token", objectMapper.writeValueAsString(alertDefinition)));
        assertThat(response2.returnResponse().getStatusLine().getStatusCode()).isEqualTo(403);

    }

    @Test
    public void testUpdateAlertAsService_failBecauseOfDifferentTeam() throws IOException {
        final AlertDefinition alert = getAlertDefinition("test6");

        final Executor executor = Executor.newInstance();
        final Response response = executor.execute(create("alert-definitions", "test-service-token", objectMapper.writeValueAsString(alert)));

        final String content = response.returnContent().asString();
        final AlertDefinition alertDefinition = objectMapper.readValue(content, AlertDefinition.class);
        alertDefinition.setName("test6-updated");
        alertDefinition.setTeam("other-team");
        alertDefinition.setResponsibleTeam("other-team");

        final Response response2 = executor.execute(update("alert-definitions/" + alertDefinition.getId(), "test-service-token", objectMapper.writeValueAsString(alertDefinition)));
        assertThat(response2.returnResponse().getStatusLine().getStatusCode()).isEqualTo(403);
    }

    private AlertDefinition getAlertDefinition(String name) {
        final AlertDefinition alert = new AlertDefinition();
        alert.setName(name);
        alert.setDescription("test");
        alert.setTeam("test-team");
        alert.setResponsibleTeam("test-team");
        alert.setEntities(Collections.emptyList());
        alert.setEntitiesExclude(Collections.emptyList());
        alert.setNotifications(Collections.emptyList());
        alert.setCheckDefinitionId(checkDefinitionId);
        alert.setStatus(DefinitionStatus.INACTIVE);
        alert.setPriority(1);
        alert.setCondition("!=True");
        return alert;
    }
}

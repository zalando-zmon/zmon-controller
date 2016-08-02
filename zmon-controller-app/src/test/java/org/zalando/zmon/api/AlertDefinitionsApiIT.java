package org.zalando.zmon.api;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.zalando.zmon.ZmonApplication;
import org.zalando.zmon.config.TestSecurityConfig;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringApplicationConfiguration(classes = {ZmonApplication.class, TestSecurityConfig.class})
@WebIntegrationTest(value = {"server.ssl.enabled=false"}, randomPort = true)
public class AlertDefinitionsApiIT {

    @Rule
    public SpringMethodRule springMethodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Value("${local.server.port}")
    private int port;

    private Request create(String body) {
        return Request.Post("http://localhost:" + port + "/api/v1/alert-definitions")
                .setHeader("Authorization", "Bearer testtoken")
                .bodyString(body, ContentType.APPLICATION_JSON);
    }

    @Test
    public void createAlertDefinitionWithoutTemplateProperty() throws IOException {
        Executor executor = Executor.newInstance();

        Response response = executor.execute(create(
                "{\"check_definition_id\":1,\"status\":\"ACTIVE\",\"team\":\"Test\",\"responsible_team\":\"Test\",\"entities\":[],\"entities_exclude\":[]}"));
        // we don't get 400 error (validation error), but 403 (not authorized because of team wrong/missing)
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(403);
    }
}

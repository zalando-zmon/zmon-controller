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
public class EntityApiIT {

    @Rule
    public SpringMethodRule springMethodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Value("${local.server.port}")
    private int port;

    private Request updateEntity(String body) {
        return Request.Post("http://localhost:" + port + "/api/v1/entities/")
                .setHeader("Authorization", "Bearer testtoken")
                .bodyString(body, ContentType.APPLICATION_JSON);
    }

    private Request getEntity(String id) {
        return Request.Get("http://localhost:" + port + "/api/v1/entities/" + id)
                .setHeader("Authorization", "Bearer testtoken");
    }

    private Request deleteEntity(String id) {
        return Request.Delete("http://localhost:" + port + "/api/v1/entities/" + id)
                .setHeader("Authorization", "Bearer testtoken");
    }

    @Test
    public void createUpdateDeleteEntity() throws IOException {
        Executor executor = Executor.newInstance();

        Response response = executor.execute(updateEntity(
                "{\"id\":\"testentity\",\"type\":\"test\"}"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        String content = executor.execute(getEntity("testentity")).returnContent().asString();
        assertThat(content).contains("\"id\": \"testentity\"");
        assertThat(content).doesNotContain("\"foo\": \"bar\"");

        // now update one entity property
        response = executor.execute(updateEntity(
                "{\"id\":\"testentity\",\"type\":\"test\",\"foo\":\"bar\"}"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // updating twice should work
        response = executor.execute(updateEntity(
                "{\"id\":\"testentity\",\"type\":\"test\",\"foo\":\"bar\"}"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        content = executor.execute(getEntity("testentity")).returnContent().asString();
        assertThat(content).contains("\"foo\": \"bar\"");

        // Creating entity of "type" GLOBAL is not allowed
        response = executor.execute(updateEntity(
                "{\"id\":\"testentity\",\"type\":\"GLOBAL\",\"team\":\"team1\"}"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(403);

        // Anyone other tha administrators are not allowed to create entity of "type" zmon_config
        response = executor.execute(updateEntity(
                "{\"id\":\"testentity\",\"type\":\"zmon_config\",\"team\":\"team1\"}"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(403);

        // Entity input should be properly formatted
        response = executor.execute(updateEntity(
                "{\"id\"\"testentity\",\"type\":\"test\",\"team\":\"team1\"}"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(400);

        // now set the special "team" property
        response = executor.execute(updateEntity(
                "{\"id\":\"testentity\",\"type\":\"test\",\"team\":\"team1\"}"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // we are not allowed to update the entity (we have no team and our uid is different)
        response = executor.execute(Request.Post("http://localhost:" + port + "/api/v1/entities/")
                .setHeader("Authorization", "Bearer testtoken2")
                .bodyString("{\"id\":\"testentity\",\"type\":\"test\",\"foo\":\"bar2\"}", ContentType.APPLICATION_JSON));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(403);
        content = executor.execute(getEntity("testentity")).returnContent().asString();
        assertThat(content).doesNotContain("\"foo\": \"bar2\"");

        // DELETE
        response = executor.execute(deleteEntity("testentity"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        response = executor.execute(getEntity("testentity"));
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void updateEntityWithTypeGlobalIsForbidden() throws IOException {
        Executor executor = Executor.newInstance();
        String initialType = "whatever";
        String typeToUpdate = "new_whatever";
        String jsonTemplate = "{\"id\":\"any_id\",\"type\":\"%s\"}";

        Response createResponse = executor.execute(updateEntity(String.format(jsonTemplate, initialType)));
        int okStatusCode = createResponse.returnResponse().getStatusLine().getStatusCode();
        assertThat(okStatusCode).isEqualTo(200).as("Entity should be created");

        Response updateResponse = executor.execute(updateEntity(String.format(jsonTemplate, typeToUpdate)));
        int forbiddenStatusCode = updateResponse.returnResponse().getStatusLine().getStatusCode();
        assertThat(forbiddenStatusCode).isEqualTo(409).as("Updating entity should be prohibited");
    }
}

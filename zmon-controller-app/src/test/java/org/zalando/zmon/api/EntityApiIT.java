package org.zalando.zmon.api;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.zalando.zmon.ZmonApplication;

import java.io.IOException;

@SpringApplicationConfiguration(classes = {ZmonApplication.class})
@WebIntegrationTest(value = {"server.ssl.enabled=false"}, randomPort = true)
@PropertySource("classpath:/test.properties")
public class EntityApiIT {

    @Rule
    public SpringMethodRule springMethodRule = new SpringMethodRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Value("${local.server.port}")
    private int port;

    @Test
    public void createUpdateEntity() throws IOException {
        Executor.newInstance().execute(Request.Post("http://localhost:" + port + "/api/v1/entities/"));
    }
}

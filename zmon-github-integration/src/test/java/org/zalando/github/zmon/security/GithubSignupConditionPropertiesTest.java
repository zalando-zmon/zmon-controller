package org.zalando.github.zmon.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

public class GithubSignupConditionPropertiesTest {

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void testDefaultGithubSignupConditionProperties() {
        load(TestConfiguration.class);
        GithubSignupConditionProperties properties = context.getBean(GithubSignupConditionProperties.class);
        assertThat(properties.getAllowedUsers()).isEmpty();
        assertThat(properties.getAllowedOrgas()).isEmpty();

    }

    @Test
    public void testGithubSignupConditionPropertiesWithConfiguredValues() {
        load(TestConfiguration.class, "zmon.signup.github.allowedUsers:kmeier,pmueller",
                "zmon.signup.github.allowedOrgas:zalando,zalando-stups");
        GithubSignupConditionProperties properties = context.getBean(GithubSignupConditionProperties.class);
        Assertions.assertThat(properties).isNotNull();
        assertThat(properties.getAllowedUsers()).contains("kmeier", "pmueller");
        assertThat(properties.getAllowedOrgas()).contains("zalando", "zalando-stups");

    }

    @Test
    public void testDefaultGithubSignupConditionPropertiesWithSystemPropertiesSet() {
        System.setProperty("ZMON_SIGNUP_GITHUB_ALLOWED_USERS", "rmueller,pschmidt");
        System.setProperty("ZMON_SIGNUP_GITHUB_ALLOWED_ORGAS", "zalando-incubator,zalando-zmon");
        load(TestConfiguration.class);
        GithubSignupConditionProperties properties = context.getBean(GithubSignupConditionProperties.class);
        Assertions.assertThat(properties).isNotNull();
        assertThat(properties.getAllowedUsers()).contains("rmueller", "pschmidt");
        assertThat(properties.getAllowedOrgas()).contains("zalando-incubator", "zalando-zmon");
        System.getProperties().remove("ZMON_SIGNUP_GITHUB_ALLOWED_USERS");
        System.getProperties().remove("ZMON_SIGNUP_GITHUB_ALLOWED_ORGAS");
    }

    private void load(Class<?> config, String... environment) {
        this.context = doLoad(new Class<?>[] { config }, environment);
    }

    private AnnotationConfigApplicationContext doLoad(Class<?>[] configs, String... environment) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(configs);
        EnvironmentTestUtils.addEnvironment(applicationContext, environment);
        applicationContext.refresh();
        return applicationContext;
    }

    @Configuration
    @EnableConfigurationProperties({ GithubSignupConditionProperties.class })
    protected static class TestConfiguration {
    }

}

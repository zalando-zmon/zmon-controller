package org.pkg.unknown;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.github.zmon.security.GithubSignupConditionProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { App.class })
@WebIntegrationTest
@Ignore
public class SystemPropertiesTest {

    @Autowired
    private GithubSignupConditionProperties properties;

    @Test
    public void startUp() {
        assertThat(properties.getAllowedUsers()).contains("kmeier", "pmueller");
        assertThat(properties.getAllowedOrgas()).contains("zalando", "zalando-stups");
    }

}

package org.pkg.unknown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.zalando.github.zmon.security.GithubSignupConditionProperties;

@SpringBootApplication
@EnableConfigurationProperties({ GithubSignupConditionProperties.class })
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

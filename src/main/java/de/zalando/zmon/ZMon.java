package de.zalando.zmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by jmussler on 28.04.15.
 */

@EnableAutoConfiguration
//@EnableWebMvc
@EnableWebMvcSecurity
@EnableConfigurationProperties
@Configuration
@ComponentScan
@PropertySource("db_api_version.properties")
@ImportResource({"classpath:spring/persistence.xml","classpath:spring/redis.xml",
        "classpath:spring/mail.xml","classpath:spring/jmx.xml"})
@SpringBootApplication
public class ZMon extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ZMon.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ZMon.class, args);
    }
}

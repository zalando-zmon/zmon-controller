package de.zalando.zmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Entry for Zmon.
 * 
 * @author jbellmann
 *
 */
@SpringBootApplication(exclude = { VelocityAutoConfiguration.class })
@ComponentScan(basePackages = { "de.zalando.zmon", "de.zalando.eventlog" })
public class ZmonApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ZmonApplication.class, args);
    }
}

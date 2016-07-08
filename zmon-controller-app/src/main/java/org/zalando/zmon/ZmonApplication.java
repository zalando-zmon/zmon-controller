package org.zalando.zmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Entry for Zmon.
 *
 * @author jbellmann
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.zalando.zmon"})
public class ZmonApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ZmonApplication.class, args);
    }
}

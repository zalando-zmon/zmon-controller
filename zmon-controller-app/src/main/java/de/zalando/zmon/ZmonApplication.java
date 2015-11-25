package de.zalando.zmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;

/**
 * Entry for Zmon.
 * 
 * @author jbellmann
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { VelocityAutoConfiguration.class })
public class ZmonApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ZmonApplication.class, args);
	}
}

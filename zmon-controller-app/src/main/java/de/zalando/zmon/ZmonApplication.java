package de.zalando.zmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry for Zmon.
 * 
 * @author jbellmann
 *
 */
@SpringBootApplication
public class ZmonApplication {

	public static void main(String[] args){
		SpringApplication.run(ZmonApplication.class, args);
	}
	
}

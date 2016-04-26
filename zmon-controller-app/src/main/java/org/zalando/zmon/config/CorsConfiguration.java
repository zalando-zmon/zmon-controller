package org.zalando.zmon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CorsConfiguration {

    private final Logger log = LoggerFactory.getLogger(CorsConfiguration.class);

    @Value("${endpoints.cors.allowed-origin}")
    private String corsAllowedOrigins;

    //@formatter:off
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        log.info("Configure 'corsMapping' for allowedOrigins : {}", corsAllowedOrigins);

        return new WebMvcConfigurerAdapter() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(StringUtils.commaDelimitedListToStringArray(corsAllowedOrigins))
                        .allowedMethods("PUT", "DELETE", "GET", "POST", "OPTIONS")
                        .allowCredentials(false)
                        .maxAge(3600);
            }

        };
    }
    //@formatter:on

}

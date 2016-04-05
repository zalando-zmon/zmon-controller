package org.zalando.zmon.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.zalando.zmon.util.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jbellmann
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig {

    @Bean
    public WebMvcConfigurer resourcesHandler() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // automatically serve/expose all static files in the "public" folder
                registry.addResourceHandler("/**").addResourceLocations("classpath:public/");
            }
        };
    }

    @Bean
    public WebMvcConfigurer pathMatching() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                configurer.setUseSuffixPatternMatch(false);
            }
        };
    }

    @Bean
    public ObjectMapperProvider objectMapperProvider() {
        return new ObjectMapperProvider();
    }

    @Bean
    public WebMvcConfigurer jsonMapping(ObjectMapper objectMapper) {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                // we only want to support our "lower_case" JSON properties..
                converters.clear();
                converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
            }
        };
    }

}

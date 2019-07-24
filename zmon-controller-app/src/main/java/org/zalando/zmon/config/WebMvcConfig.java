package org.zalando.zmon.config;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
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

    private final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

    @Bean
    public WebMvcConfigurer resourcesHandler() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                final String localStaticResources = "file:" + Paths.get("zmon-controller-ui").toAbsolutePath().toString() + "/";
                log.info("Using local static resources from {}", localStaticResources);

                // automatically serve/expose all static files in the "public" folder
                registry.addResourceHandler("/**").addResourceLocations(localStaticResources, "classpath:public/");
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
                StringHttpMessageConverter converter = new StringHttpMessageConverter();
                List<MediaType> types = Arrays.asList(
                        new MediaType("text", "plain", Charset.forName("UTF-8")),
                        new MediaType("application", "javascript")
                );
                converter.setSupportedMediaTypes(types);
                converters.add(converter);
            }
        };
    }

}

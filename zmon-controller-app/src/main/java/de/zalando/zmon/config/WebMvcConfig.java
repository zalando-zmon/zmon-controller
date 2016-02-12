package de.zalando.zmon.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.zmon.util.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author jbellmann
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig {

    @Bean
    public WebMvcConfigurer resourcesHandler() {
        final CacheControl thirtyDays = CacheControl.maxAge(30, TimeUnit.DAYS);
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/js/**").addResourceLocations("classpath:static/js/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/lib/**").addResourceLocations("classpath:static/lib/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/styles/**").addResourceLocations("classpath:static/styles/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/asset/**").addResourceLocations("classpath:static/asset/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/templates/**").addResourceLocations("classpath:static/templates/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/views/**").addResourceLocations("classpath:static/views/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/logo.png").addResourceLocations("classpath:static/logo.png")
                        .setCacheControl(thirtyDays);

                // GRAFANA
                registry.addResourceHandler("/grafana/app/**").addResourceLocations("classpath:static/grafana/app/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana/css/**").addResourceLocations("classpath:static/grafana/css/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana/fonts/**").addResourceLocations("classpath:static/grafana/fonts/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana/img/**").addResourceLocations("classpath:static/grafana/img/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana/plugins/**")
                        .addResourceLocations("classpath:static/grafana/plugins/").setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana/vendor/**")
                        .addResourceLocations("classpath:static/grafana/vendor/").setCacheControl(thirtyDays);

                // GRAFANA 2
                registry.addResourceHandler("/grafana2/app/**").addResourceLocations("classpath:static/grafana2/app/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/css/**").addResourceLocations("classpath:static/grafana2/css/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/dashboards/**")
                        .addResourceLocations("classpath:static/grafana2/dashboards/").setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/fonts/**")
                        .addResourceLocations("classpath:static/grafana2/fonts/").setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/img/**").addResourceLocations("classpath:static/grafana2/img/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/vendor/**")
                        .addResourceLocations("classpath:static/grafana2/vendor/").setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/views/**")
                        .addResourceLocations("classpath:static/grafana2/views/").setCacheControl(thirtyDays);

                registry.setOrder(Integer.MIN_VALUE + 5);

            }
        };
    }

    // see, resources /spring/mvc.xml
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

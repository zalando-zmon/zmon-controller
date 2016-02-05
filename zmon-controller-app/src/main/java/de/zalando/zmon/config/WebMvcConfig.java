package de.zalando.zmon.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
 * @author jbellmann
 *
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig { // extends WebMvcConfigurationSupport {

    // @Override
    // public void configurePathMatch(PathMatchConfigurer configurer) {
    // configurer.setUseSuffixPatternMatch(false);
    // }
    //
    // @Override
    // protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    // final CacheControl thirtyDays = CacheControl.maxAge(30, TimeUnit.DAYS);
    // registry.addResourceHandler("/js/**").addResourceLocations("classpath:/public/js/").setCacheControl(thirtyDays);
    //
    // registry.addResourceHandler("/lib/**").addResourceLocations("classpath:/public/lib/")
    //
    // .setCacheControl(thirtyDays);
    //
    // registry.addResourceHandler("/styles/**").addResourceLocations("classpath:/public/styles/")
    // .setCacheControl(thirtyDays);
    //
    // registry.setOrder(Integer.MIN_VALUE + 5);
    // }

    @Bean
    public WebMvcConfigurer resourcesHandler() {
        final CacheControl thirtyDays = CacheControl.maxAge(30, TimeUnit.DAYS);
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // TODO: Remove unnecessary ResourceHander Config due to Spring
                // Boot's automatic resource handling caps
                registry.addResourceHandler("/js/**").addResourceLocations("classpath:/public/js/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/lib/**").addResourceLocations("classpath:/public/lib/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/styles/**").addResourceLocations("classpath:/public/styles/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/asset/**").addResourceLocations("classpath:/public/asset/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/public/templates/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/views/**").addResourceLocations("classpath:/public/views/")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/logo.png").addResourceLocations("classpath:/public/logo.png")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana/**").addResourceLocations("classpath:/public/grafana")
                        .setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/**").addResourceLocations("classpath:/public/grafana2")
                        .setCacheControl(thirtyDays);

                registry.setOrder(Integer.MIN_VALUE + 5);
            }
        };
    }

    // see, resoureces /spring/mvc.xml
    @Bean
    public WebMvcConfigurer pathMatching() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                configurer.setUseSuffixPatternMatch(false);
            }
        };
    }

}

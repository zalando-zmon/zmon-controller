package de.zalando.zmon.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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
public class WebMvcConfig {// extends WebMvcConfigurerAdapter {

    // @Autowired
    // private Environment env;
    //
    // @Value("${resources.projectroot:}")
    // private String projectRoot;
    //
    // @Value("${app.version:13}")
    // private String appVersion;
    //
//    //@formatter:off
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        boolean devMode = this.env.acceptsProfiles("development");
//
//        String location = devMode ? "file:///" + getProjectRootRequired() + "/zmon-controller-ui/" : "classpath:static/";
//        Integer cachePeriod = devMode ? 0 : null;
//        boolean useResourceCache = !devMode;
//        String version = getApplicationVersion();
//
//        AppCacheManifestTransformer appCacheTransformer = new AppCacheManifestTransformer();
//        VersionResourceResolver versionResolver = new VersionResourceResolver()
//                .addFixedVersionStrategy(version, "/**/*.js", "/**/*.map")
//                    .addContentVersionStrategy("/**");
//
//        registry.addResourceHandler("/**")
//                    .addResourceLocations(location)
//                        .setCachePeriod(cachePeriod)
//                            .resourceChain(useResourceCache)
//                                .addResolver(versionResolver)
//                                       .addTransformer(appCacheTransformer);
//    }
//    //@formatter:on
    //
    // private String getProjectRootRequired() {
    // Assert.state(this.projectRoot != null, "Please set
    // \"resources.projectRoot\" in application.yml");
    // return this.projectRoot;
    // }
    //
    // protected String getApplicationVersion() {
    // return this.env.acceptsProfiles("development") ? "dev" : this.appVersion;
    // }
    //
    // @Override
    // public void configurePathMatch(PathMatchConfigurer configurer) {
    // configurer.setUseSuffixPatternMatch(false);
    // }

    // @Bean
    // public WebMvcConfigurer webConfigurer() {
    // return new WebMvcConfigurerAdapter() {
    //
    //
    // };
    // }

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

                registry.addResourceHandler("/grafana2/app/**").addResourceLocations("classpath:static/grafana2/app/",
                        "classpath:static/grafana2/app/components").setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/public/**")
                        .addResourceLocations("classpath:static/grafana2/public/").setCacheControl(thirtyDays);

                registry.addResourceHandler("/grafana2/img/**").addResourceLocations("classpath:static/grafana2/img/")
                        .setCacheControl(thirtyDays);

                registry.setOrder(Integer.MIN_VALUE + 5);
            }
        };
    }

    // see, resoureces /spring/mvc.xml
    // @Bean
    // public WebMvcConfigurer pathMatching() {
    // return new WebMvcConfigurerAdapter() {
    // @Override
    // public void configurePathMatch(PathMatchConfigurer configurer) {
    // configurer.setUseSuffixPatternMatch(false);
    // }
    // };
    // }

}

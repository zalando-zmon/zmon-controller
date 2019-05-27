package org.zalando.zmon.config;

import io.opentracing.contrib.spring.web.client.TracingAsyncRestTemplateInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;
import org.zalando.riptide.httpclient.RestAsyncClientHttpRequestFactory;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * Created by hjacobs on 1/28/16.
 */
@Configuration
@EnableConfigurationProperties({KairosDBProperties.class})
public class KairosDBConfiguration {

    private final KairosDBProperties kairosDBProperties;

    @Autowired
    public KairosDBConfiguration(KairosDBProperties kairosDBProperties) {
        this.kairosDBProperties = kairosDBProperties;
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        final CloseableHttpClient client = kairosDBProperties.getHttpClient();
        final ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(newCachedThreadPool());
        final AsyncRestTemplate restTemplate = new AsyncRestTemplate(new RestAsyncClientHttpRequestFactory(client, executor));

        restTemplate.getInterceptors().add(new TracingAsyncRestTemplateInterceptor());
        // Since we set our own CORS headers (CorsConfiguration), strip them from KairosDB response
        restTemplate.getInterceptors().add(new CorsHeadersCleanerInterceptors());

        return restTemplate;
    }
}

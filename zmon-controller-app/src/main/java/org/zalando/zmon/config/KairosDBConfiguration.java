package org.zalando.zmon.config;

import io.opentracing.contrib.spring.web.client.TracingAsyncRestTemplateInterceptor;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Created by hjacobs on 1/28/16.
 */
@Configuration
@EnableConfigurationProperties({KairosDBProperties.class})
public class KairosDBConfiguration {

    @Autowired
    KairosDBProperties kairosDBProperties;

    /**
     * Maybe we will need more configuration in the future, so let it do here.
     *
     * @return instead of using
     *         {@link HttpComponentsAsyncClientHttpRequestFactory} you can also
     *         use:
     * @see Netty4ClientHttpRequestFactory
     * @see OkHttpClientHttpRequestFactory
     */
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        CloseableHttpAsyncClient client = kairosDBProperties.getHttpAsyncClient();
        final AsyncRestTemplate restTemplate = new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory(client));

        restTemplate.getInterceptors().add(new TracingAsyncRestTemplateInterceptor());
        return restTemplate;
    }
}

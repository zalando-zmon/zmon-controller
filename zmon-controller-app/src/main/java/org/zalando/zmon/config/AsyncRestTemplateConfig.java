package org.zalando.zmon.config;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;

@Configuration
public class AsyncRestTemplateConfig {

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
        CloseableHttpAsyncClient client = HttpAsyncClients.createSystem();
        return new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory(client));
    }
}

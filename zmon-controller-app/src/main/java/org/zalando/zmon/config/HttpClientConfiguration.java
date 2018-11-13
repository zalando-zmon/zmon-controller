package org.zalando.zmon.config;

import io.opentracing.contrib.apache.http.client.TracingHttpClientBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class HttpClientConfiguration {

    private static final int SOCKET_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 1000;
    private static final int MAX_CONN_PER_ROUTE = 100;
    private static final int MAX_CONN_TOTAL = 200;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public HttpClient httpClient() {
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .build();
        return new TracingHttpClientBuilder().
                setMaxConnPerRoute(MAX_CONN_PER_ROUTE).
                setMaxConnTotal(MAX_CONN_TOTAL).
                setDefaultRequestConfig(config)
                .build();
    }
}

package org.zalando.zmon.config;

import io.opentracing.contrib.apache.http.client.TracingHttpClientBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hjacobs on 2/5/16.
 */
@Configuration
@EnableConfigurationProperties({MetricCacheProperties.class})
public class MetricCacheConfiguration {
    @Bean
    public HttpClient metricCacheHttpClient(final MetricCacheProperties props) {
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(props.getSocketTimeout())
                .setConnectTimeout(props.getConnectTimeout())
                .build();
        return new TracingHttpClientBuilder().
                setMaxConnPerRoute(props.getMaxConnPerRoute()).
                setMaxConnTotal(props.getMaxConnTotal()).
                setDefaultRequestConfig(config)
                .build();
    }
}

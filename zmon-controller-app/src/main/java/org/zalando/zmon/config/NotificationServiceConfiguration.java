package org.zalando.zmon.config;

import io.opentracing.contrib.apache.http.client.TracingHttpClientBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by hjacobs on 2/5/16.
 */
@Configuration
@EnableConfigurationProperties({NotificationServiceProperties.class})
public class NotificationServiceConfiguration {
    @Bean
    public HttpClient notificationServiceHttpClient(final NotificationServiceProperties props) {
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(props.getSocketTimeout())
                .setConnectTimeout(props.getConnectTimeout())
                .build();
        return new TracingHttpClientBuilder().
                setMaxConnPerRoute(props.getMaxConnPerRoute()).
                setMaxConnTotal(props.getMaxConnTotal()).
                setConnectionTimeToLive(props.getConnectionTimeToLive(), TimeUnit.MILLISECONDS).
                setDefaultRequestConfig(config)
                .build();
    }
}

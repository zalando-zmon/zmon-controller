package org.zalando.zmon.config;

import io.opentracing.contrib.apache.http.client.TracingHttpClientBuilder;
import io.opentracing.contrib.spring.web.client.TracingRestTemplateInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.zalando.zmon.persistence.AlertDefinitionSProcService;
import org.zalando.zmon.persistence.CheckDefinitionSProcService;
import org.zalando.zmon.security.permission.DefaultZMonPermissionService;
import org.zalando.zmon.service.EventLogService;
import org.zalando.zmon.service.HistoryService;
import org.zalando.zmon.service.impl.HistoryServiceImpl;
import org.zalando.zmon.service.impl.NoOpEventLog;

import java.util.concurrent.TimeUnit;

/**
 * TODO, we have to replace EventLog.
 *
 * @author jbellmann
 */
@Configuration
@EnableConfigurationProperties({EventLogProperties.class})
public class EventLogConfiguration {

    @Bean
    public NoOpEventLog noOpEventLog() {
        return new NoOpEventLog();
    }

    @Bean
    public RestOperations restOperations(final EventLogProperties config) {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory(config));
        restTemplate.getInterceptors().add(new TracingRestTemplateInterceptor());

        return restTemplate;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(final EventLogProperties config) {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getConnectTimeout())
                .setConnectionRequestTimeout(config.getRequestConnectTimeout())
                .setSocketTimeout(config.getSocketTimeout())
                .build();
        final CloseableHttpClient client = new TracingHttpClientBuilder().
                setConnectionTimeToLive(config.getConnectionTimeToLive(), TimeUnit.MILLISECONDS).
                setDefaultRequestConfig(requestConfig).build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }

    @Bean
    @Transactional
    public HistoryService historyService(final CheckDefinitionSProcService checkDefinitionSProc,
                                         final AlertDefinitionSProcService alertDefinitionSProc,
                                         final EventLogService eventLog) {
        return new HistoryServiceImpl(checkDefinitionSProc, alertDefinitionSProc, eventLog);
    }
}

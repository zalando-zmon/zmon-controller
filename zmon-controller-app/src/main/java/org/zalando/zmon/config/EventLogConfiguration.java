package org.zalando.zmon.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.zalando.zmon.service.HistoryService;
import org.zalando.zmon.service.impl.HistoryServiceImpl;
import org.zalando.zmon.service.impl.NoOpEventLog;

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
        return new RestTemplate(getClientHttpRequestFactory(config));
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory(final EventLogProperties config) {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getConnectTimeout())
                .setConnectionRequestTimeout(config.getRequestConnectTimeout())
                .setSocketTimeout(config.getSocketTimeout())
                .build();
        final CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(requestConfig)
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }

    @Bean
    @Transactional
    public HistoryService historyService(final RestOperations restOperations,
                                         final CheckDefinitionSProcService checkDefinitionSProc,
                                         final AlertDefinitionSProcService alertDefinitionSProc,
                                         final EventLogProperties eventLogProperties) {
        return new HistoryServiceImpl(restOperations, checkDefinitionSProc, alertDefinitionSProc, eventLogProperties);
    }
}

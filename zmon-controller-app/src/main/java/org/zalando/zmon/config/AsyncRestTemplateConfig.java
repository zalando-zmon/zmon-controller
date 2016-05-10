package org.zalando.zmon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;

@Configuration
public class AsyncRestTemplateConfig {

    /**
     * Maybe we will need more configuration in the future, so let it do here.
     * 
     * @return
     */
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory());
    }
}

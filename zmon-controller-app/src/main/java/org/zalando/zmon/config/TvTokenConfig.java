package org.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.zmon.persistence.OnetimeTokensSProcService;
import org.zalando.zmon.security.tvtoken.TvTokenService;

@Configuration
public class TvTokenConfig {

    @Autowired
    private OnetimeTokensSProcService onetimeTokensSProcService;

    @Bean
    public TvTokenService tvTokenService() {
        return new TvTokenService(onetimeTokensSProcService);
    }

}

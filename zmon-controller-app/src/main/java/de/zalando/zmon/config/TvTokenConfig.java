package de.zalando.zmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.zalando.zmon.persistence.OnetimeTokensSProcService;
import de.zalando.zmon.security.tvtoken.TvTokenService;

@Configuration
public class TvTokenConfig {

    @Autowired
    private OnetimeTokensSProcService onetimeTokensSProcService;

    @Bean
    public TvTokenService tvTokenService() {
        return new TvTokenService(onetimeTokensSProcService);
    }

}

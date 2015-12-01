package de.zalando.zauth.zmon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

/**
 * 
 * 
 * 
 * @author jbellmann
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${security.oauth2.resource.userInfoUri}")
    private String tokenInfoUri;

    /**
     * Configure scopes for specific controller/httpmethods/roles here.
     */
    @Override
    public void configure(final HttpSecurity http) throws Exception {

        //J-
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
            .authorizeRequests()
            	.antMatchers(HttpMethod.GET, "/rest/**").access("#oauth2.hasScope('uid') || #oauth2.hasScope('zmon.read')")
                .antMatchers(HttpMethod.POST, "/rest/**").access("#oauth2.hasScope('uid') || #oauth2.hasScope('zmon.write')")
                .antMatchers(HttpMethod.PUT, "/refoles/**").access("#oauth2.hasScope('uid') || #oauth2.hasScope('zmon.write')")
                .antMatchers(HttpMethod.DELETE, "/refoles/**").access("#oauth2.hasScope('uid') || #oauth2.hasScope('zmon.write')");

        //J+
    }

    @Bean
    public ResourceServerTokenServices customResourceTokenServices() {
        return new TokenInfoResourceServerTokenServices(tokenInfoUri, "zmon");
    }

}

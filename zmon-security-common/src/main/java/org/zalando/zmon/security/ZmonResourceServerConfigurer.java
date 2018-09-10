package org.zalando.zmon.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;
import org.zalando.stups.oauth2.spring.security.expression.ExtendedOAuth2WebSecurityExpressionHandler;

/**
 * 
 * @author jbellmann
 *
 */
public class ZmonResourceServerConfigurer extends ResourceServerConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(ZmonResourceServerConfigurer.class);

    private final ResourceServerTokenServices resourceServerTokenServices;

    public ZmonResourceServerConfigurer(ResourceServerTokenServices resourceServerTokenServices) {
        Assert.notNull(resourceServerTokenServices, "'ResourceServerTokenService' should never be null");
        this.resourceServerTokenServices = resourceServerTokenServices;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("zmon").tokenServices(resourceServerTokenServices); // .stateless(false);
        // here is the important part
        resources.expressionHandler(new ExtendedOAuth2WebSecurityExpressionHandler());
    }

    //@formatter:off
	/**
	 * Configure scopes for specific controller/httpmethods/roles here.
	 */
	@Override
	public void configure(final HttpSecurity http) throws Exception {
		log.info("Configuring OAuth..");
		// J-
		http
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		.and()
			.requestMatchers()
				.antMatchers("/api/v1/**", "/grafana", "/grafana/dashboard/db/**", "/rest/grafana", "/rest/kairosdbs/**")
		.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/api/v1/**")
					.access("(#oauth2.hasScope('uid') && #oauth2.hasAnyRealm('/employees','/services')) or #oauth2.hasScope('zmon.read_all')")
				.antMatchers(HttpMethod.POST, "/api/v1/**")
					.access("(#oauth2.hasScope('uid') && #oauth2.hasAnyRealm('/employees','/services')) or #oauth2.hasScope('zmon.write_all')")
				.antMatchers(HttpMethod.PUT, "/api/v1/**")
					.access("(#oauth2.hasScope('uid') && #oauth2.hasAnyRealm('/employees','/services')) or #oauth2.hasScope('zmon.write_all')")
				.antMatchers(HttpMethod.DELETE, "/api/v1/**")
					.access("(#oauth2.hasScope('uid') && #oauth2.hasAnyRealm('/employees','/services')) or #oauth2.hasScope('zmon.write_all')");

		// J+
	}
	//@formatter:on

}

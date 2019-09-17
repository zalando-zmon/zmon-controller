package org.zalando.zauth.zmon.config;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;
import org.zalando.zmon.security.*;
import org.zalando.zmon.security.authority.ZMonRole;
import org.zalando.zmon.security.grafanatoken.GrafanaRememberMeServices;
import org.zalando.zmon.security.grafanatoken.GrafanaTokenService;
import org.zalando.zmon.security.jwt.JWTRememberMeServices;
import org.zalando.zmon.security.jwt.JWTService;
import org.zalando.zmon.security.rememberme.MultiRememberMeServices;
import org.zalando.zmon.security.service.ChainedResourceServerTokenServices;
import org.zalando.zmon.security.service.PresharedTokensResourceServerTokenServices;
import org.zalando.zmon.security.service.SimpleSocialUserDetailsService;
import org.zalando.zmon.security.tvtoken.TvTokenService;
import org.zalando.zmon.security.tvtoken.ZMonTvRememberMeServices;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jbellmann
 */
@Configuration
@EnableWebSecurity
// The EnableResourceServer creates a WebSecurityConfigurerAdapter with a
// hard-coded Order (of 3).
@EnableResourceServer
@Order(5)
public class ZauthSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PAGE_URL = "/signin";
    public static final String LOGIN_COOKIE_NAME = "JSESSIONID";

    private final AuthorityService authorityService;

    private final Environment environment;

    private final TvTokenService TvTokenService;

    private final JWTService jwtService;

    private final GrafanaTokenService grafanaTokenService;

    private final TeamService teamService;

    private final DynamicTeamService dynamicTeamService;

    @Autowired
    public ZauthSecurityConfig(AuthorityService authorityService,
                               Environment environment,
                               TvTokenService TvTokenService,
                               JWTService jwtService,
                               GrafanaTokenService grafanaTokenService,
                               TeamService teamService,
                               DynamicTeamService dynamicTeamService) {
        this.authorityService = authorityService;
        this.environment = environment;
        this.TvTokenService = TvTokenService;
        this.jwtService = jwtService;
        this.grafanaTokenService = grafanaTokenService;
        this.teamService = teamService;
        this.dynamicTeamService = dynamicTeamService;
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsManager());
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**").antMatchers(WebSecurityConstants.IGNORED_PATHS);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // J-
    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http = http
                .authenticationProvider(new RememberMeAuthenticationProvider("ZMON_TV"))
                .authenticationProvider(new RememberMeAuthenticationProvider("ZMON_JWT"))
                .authenticationProvider(new RememberMeAuthenticationProvider("GRAFANA_JWT"));

        http
        .apply(new SpringSocialConfigurer())
        .and()
            .exceptionHandling()
                .authenticationEntryPoint(zmonAuthenticationEntryPoint())
        .and()
            .formLogin()
                .loginPage(LOGIN_PAGE_URL)
                .failureUrl("/signin?error=bad_credentials")
                .permitAll()
        .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies(LOGIN_COOKIE_NAME)
                .logoutSuccessUrl("/signin?logout=yes")
                .permitAll()
        .and()
            .authorizeRequests()
                .antMatchers("/grafana6/**")
                    .permitAll()
        .and()
            .authorizeRequests()
                .antMatchers("/rest/kairosdbs/**", "/rest/checkAlertResults", "/rest/checkResultsWithoutEntities", "/rest/alertDetails", "/rest/alertHistory")
                    .authenticated()
                .anyRequest()
                    .access("authenticated AND !hasAuthority('"+ZMonRole.KAIROS_READER.getRoleName()+"')")
        .and()
            .rememberMe()
            .rememberMeServices(new MultiRememberMeServices(
                    new JWTRememberMeServices(jwtService),
                    new ZMonTvRememberMeServices(TvTokenService),
                    new GrafanaRememberMeServices(grafanaTokenService)
            ))
        .and()
            .csrf()
                .disable()
            .headers()
                .frameOptions()
                    .disable();
    }
    // @formatter:on
    // J+

    @Bean
    public AuthenticationEntryPoint zmonAuthenticationEntryPoint() {
        return new ZmonAuthenticationEntrypoint(LOGIN_PAGE_URL);
    }

    @Bean
    public SocialUserDetailsService socialUserDetailsService() {
        return new SimpleSocialUserDetailsService(userDetailsService(), teamService);
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsManager();
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        return new InMemoryUserDetailsManager(new ArrayList<>());
    }

    @Bean
    public ResourceServerConfigurer zmonResourceServerConfigurer() {
        String tokenInfoUri = environment.getProperty("security.oauth2.resource.userInfoUri");

        final ZmonAuthenticationExtractor extractor = new ZmonAuthenticationExtractor(authorityService, dynamicTeamService);
        final List<ResourceServerTokenServices> chain = ImmutableList.of(
                new PresharedTokensResourceServerTokenServices(authorityService, environment),
                new TokenInfoResourceServerTokenServices(tokenInfoUri, extractor));

        return new ZmonResourceServerConfigurer(new ChainedResourceServerTokenServices(chain));
    }

}

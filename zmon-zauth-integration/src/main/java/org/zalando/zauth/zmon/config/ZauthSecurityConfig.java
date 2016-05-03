package org.zalando.zauth.zmon.config;

import java.util.ArrayList;

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
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;
import org.zalando.zmon.security.TeamService;
import org.zalando.zmon.security.WebSecurityConstants;
import org.zalando.zmon.security.ZmonAuthenticationEntrypoint;
import org.zalando.zmon.security.ZmonResourceServerConfigurer;
import org.zalando.zmon.security.service.SimpleSocialUserDetailsService;
import org.zalando.zmon.security.tvtoken.TvTokenService;
import org.zalando.zmon.security.tvtoken.ZMonTvRememberMeServices;

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

    @Autowired
    private Environment environment;

    @Autowired
    private TvTokenService TvTokenService;

    @Autowired
    private TeamService teamService;

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsManager());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
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

        http = http.authenticationProvider(new RememberMeAuthenticationProvider("ZMON_TV"));

        http
        .apply(new SpringSocialConfigurer())
        .and()
            .exceptionHandling()
                .authenticationEntryPoint(zmonAuthenticationEntryPoint())
        .and()
            .formLogin()
                .loginPage(LOGIN_PAGE_URL)
                .failureUrl("/signin?param.error=bad_credentials")
                .permitAll()
        .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/signin?param.logout=logout")
                .permitAll()
        .and()
            .authorizeRequests()
                .antMatchers("/**")
                .authenticated()
        .and()
            .rememberMe()
            .rememberMeServices(new ZMonTvRememberMeServices(TvTokenService))
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
        return new SimpleSocialUserDetailsService(userDetailsService());
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
        return new ZmonResourceServerConfigurer(
                new TokenInfoResourceServerTokenServices(tokenInfoUri, new ZmonAuthenticationExtractor(teamService)));
    }

}

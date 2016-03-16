package de.zalando.zauth.zmon.config;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
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
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;
import org.zalando.zmon.security.ZmonResourceServerConfigurer;
import org.zalando.zmon.security.service.SimpleSocialUserDetailsService;

import com.google.common.collect.Sets;

import de.zalando.zmon.security.TeamService;
import de.zalando.zmon.security.WebSecurityConstants;
import de.zalando.zmon.security.tvtoken.TvTokenService;
import de.zalando.zmon.security.tvtoken.ZMonTvRememberMeServices;

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

    @Autowired
    private Environment environment;

    @Autowired
    private TvTokenService TvTokenService;

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsManager());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers(WebSecurityConstants.IGNORED_PATHS);
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
            .formLogin()
                .loginPage("/signin")
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
                .disable();
    }
    // @formatter:on
    // J+

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

        //TODO, we need a real 'teamService' here
        TeamService teamService = new TeamService() {

            @Override
            public Set<String> getTeams(String username) {
                return Sets.newHashSet();
            }
        };

        return new ZmonResourceServerConfigurer(
                new TokenInfoResourceServerTokenServices(tokenInfoUri, new ZmonAuthenticationExtractor(teamService)));
    }

}

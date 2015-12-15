package de.zalando.zauth.zmon.config;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import org.zalando.stups.oauth2.spring.server.LaxAuthenticationExtractor;
import org.zalando.stups.oauth2.spring.server.TokenInfoResourceServerTokenServices;

import org.zalando.zmon.security.ZmonResourceServerConfigurer;
import org.zalando.zmon.security.service.SimpleSocialUserDetailsService;

/**
 * @author  jbellmann
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
@Order(1)
public class ZauthSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsManager());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/logo.png", "/favicon.ico", "/static-resources/**", "/asset/**", "/styles/**",
            "/css/**", "/js/**");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // J-
    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http.formLogin().loginPage("/signin").failureUrl("/signin?param.error=bad_credentials").permitAll().and()
            .logout().logoutUrl("/logout").deleteCookies("JSESSIONID").logoutSuccessUrl("/signin?param.logout=logout")
            .permitAll().and().authorizeRequests().antMatchers("/**").authenticated().and().rememberMe().and()
            .apply(new SpringSocialConfigurer()).and().csrf().disable();
    }
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
        return new InMemoryUserDetailsManager(new ArrayList<UserDetails>());
    }

    @Bean
    public ResourceServerConfigurer zmonResourceServerConfigurer() {
        String tokenInfoUri = environment.getProperty("security.oauth2.resource.userInfoUri");
        return new ZmonResourceServerConfigurer(new TokenInfoResourceServerTokenServices(tokenInfoUri,
                    new ZmonAuthorizationExtractor()));
    }

    /**
     * We allow 'uid'-scope not needed here for tokens.<br/>
     * We use 'services' as principal when 'realm' is 'services'.
     *
     * @author  jbellmann
     */
    static final class ZmonAuthorizationExtractor extends LaxAuthenticationExtractor {

        @Override
        protected Object getPrincipal(final Map<String, Object> map) {
            if (map.get("realm") != null) {
                String realm = (String) map.get("realm");
                if ("services".equals(realm)) {
                    return realm;
                }
            }

            return super.getPrincipal(map);
        }

        // no 'uid' needed
        @Override
        public boolean isThrowExceptionOnEmptyUid() {
            return false;
        }
    }
}

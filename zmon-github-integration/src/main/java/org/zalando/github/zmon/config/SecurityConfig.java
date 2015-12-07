package org.zalando.github.zmon.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;
import org.zalando.github.zmon.service.SimpleSocialUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//	@Autowired
//	private Environment environment;

	@Autowired
	public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsManager());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
				.antMatchers("/logo.png","/favicon.ico", "/static-resources/**", "/asset/**", "/styles/**", "/css/**","/js/**");
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// J-
	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		http
			.formLogin()
				.loginPage("/signin")
//				.loginProcessingUrl("/signin/authtenticate")
				.failureUrl("/signin?param.error=bad_credentials")
				.permitAll()
		.and()
			.logout()
				.logoutUrl("/logout")
				.deleteCookies("JSESSIONID")
				.permitAll()
		.and()
			.authorizeRequests()
				.antMatchers("/**")
				.authenticated()
		.and()
			.rememberMe()
		.and()
			.apply(new SpringSocialConfigurer())
		.and()
			.csrf()
				.disable();
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
//		String tokenInfoUri = environment.getProperty("security.oauth2.resource.userInfoUri");
		return new ZmonResourceServerConfigurer(new DefaultTokenServices());
	}
}

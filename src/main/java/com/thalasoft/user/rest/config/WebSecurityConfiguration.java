package com.thalasoft.user.rest.config;

import com.thalasoft.toolbox.condition.EnvProd;
import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.AuthenticationFromCredentialsFilter;
import com.thalasoft.user.rest.security.AuthenticationFromTokenFilter;
import com.thalasoft.user.rest.security.CustomAuthenticationProvider;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.UserDomainConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnvProd
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled=true) TODO see what it is used
// @EnableGlobalMethodSecurity(securedEnabled = true) TODO see what it is used
// for
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.security",
		"com.thalasoft.user.rest.filter" })
public class WebSecurityConfiguration {

	@Order(1)
	@Configuration
	public static class CredentialsConfiguration extends WebSecurityConfigurerAdapter {

		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}
	
		public AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter() throws Exception {
			AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter = new AuthenticationFromCredentialsFilter();
			authenticationFromCredentialsFilter.setAuthenticationManager(authenticationManagerBean());
			return authenticationFromCredentialsFilter;
		}
	
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher(RESTConstants.SLASH + RESTConstants.API + RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + UserDomainConstants.LOGIN)
			.addFilterBefore(authenticationFromCredentialsFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.antMatchers(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + UserDomainConstants.LOGIN).permitAll()
			.anyRequest().authenticated();
		}
	}

	@Order(2)
	@Configuration
	public static class TokenConfiguration extends WebSecurityConfigurerAdapter {

		@Autowired
		private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;
	
		@Autowired
		private SimpleCORSFilter simpleCORSFilter;
	
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}
	
		public AuthenticationFromTokenFilter authenticationFromTokenFilter() throws Exception {
			AuthenticationFromTokenFilter authenticationFromTokenFilter = new AuthenticationFromTokenFilter();
			authenticationFromTokenFilter.setAuthenticationManager(authenticationManagerBean());
			return authenticationFromTokenFilter;
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// TODO For now allow non https access as the browser non signed certificate
			// warning sucks
			// Maybe someday we'll have a signed certificate...
			// http.requiresChannel().antMatchers(RESTConstants.SLASH + RESTConstants.API +
			// "/**").requiresSecure();
	
			http.csrf().disable();
	
			http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	
			http.headers().cacheControl().disable().frameOptions().disable();
	
			http.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint);
	
			http.addFilterBefore(simpleCORSFilter, UsernamePasswordAuthenticationFilter.class);
	
			http.antMatcher(RESTConstants.SLASH + RESTConstants.API + RESTConstants.SLASH + "**")
			.addFilterBefore(authenticationFromTokenFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.antMatchers(RESTConstants.SLASH).permitAll()
			.antMatchers(RESTConstants.SLASH + RESTConstants.ERROR).permitAll()
			.antMatchers(RESTConstants.SLASH + UserDomainConstants.ADMINS + RESTConstants.SLASH + "**").hasRole(UserDomainConstants.ROLE_ADMIN)
			.anyRequest().authenticated();
		}
	}

	// @Autowired
	// private UserDetailsService userDetailsService;

	// @Autowired
	// private CustomAuthenticationProvider customAuthenticationProvider;

	// @Override
	// protected void configure(AuthenticationManagerBuilder
	// authenticationManagerBuilder) throws Exception {
	// authenticationManagerBuilder.authenticationProvider(new
	// CustomAuthenticationProvider());
	// //
	// authenticationManagerBuilder.parentAuthenticationManager(authenticationManager).authenticationProvider(customAuthenticationProvider);
	// }

	// @Bean
	// public CustomAuthenticationProvider customAuthenticationProvider() {
	// return new CustomAuthenticationProvider();
	// }

	// @Autowired
	// public void configureGlobal(AuthenticationManagerBuilder auth) throws
	// Exception {
	// auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	// }

}

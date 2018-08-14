package com.thalasoft.user.rest.security;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.AuthenticationFromCredentialsFilter;
import com.thalasoft.user.rest.security.AuthenticationFromTokenFilter;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.UserDomainConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

@EnableWebSecurity
// @EnableGlobalMethodSecurity( // TODO What is this for ?
//         securedEnabled = true,
//         jsr250Enabled = true,
//         prePostEnabled = true
// )
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.service", "com.thalasoft.user.rest.filter" })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Autowired
	private SimpleCORSFilter simpleCORSFilter;
	
	@Autowired
	private AuthenticationFromTokenFilter authenticationFromTokenFilter;

    @Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter() throws Exception {
		AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter = new AuthenticationFromCredentialsFilter(new AntPathRequestMatcher("/users/login", RequestMethod.POST.name()));
		authenticationFromCredentialsFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFromCredentialsFilter;
	}
	
	@Bean
	public AuthenticationFromTokenFilter authenticationFromTokenFilter() throws Exception {
		AuthenticationFromTokenFilter authenticationFromTokenFilter = new AuthenticationFromTokenFilter(new NegatedRequestMatcher(new AntPathRequestMatcher("/users/login")));
		authenticationFromTokenFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFromTokenFilter;
	}

	@Bean
	FilterRegistrationBean<AuthenticationFromTokenFilter> disableAutoRegistration(final AuthenticationFromTokenFilter filter) {
		final FilterRegistrationBean<AuthenticationFromTokenFilter> registration = new FilterRegistrationBean<AuthenticationFromTokenFilter>(filter);
		registration.setEnabled(false);
		return registration;
	}
  
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO For now allow non https access as the browser non signed certificate
		// warning sucks
		// Maybe someday we'll have a signed certificate...
		// http.requiresChannel().antMatchers(RESTConstants.SLASH + RESTConstants.API +
		// "/**").requiresSecure();

		// http.headers().cacheControl(); TODO what is this used for ?
		// http.headers().cacheControl().disable().frameOptions().disable();

		http.cors();

		http
		.csrf().disable()
		.formLogin().disable()
		.httpBasic().disable()
		.logout().disable();

		http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);
					
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.addFilterBefore(simpleCORSFilter, ChannelProcessingFilter.class);
		
		http
		.addFilterBefore(authenticationFromTokenFilter, UsernamePasswordAuthenticationFilter.class)
		.authorizeRequests()
		.antMatchers("/", "/error").permitAll()
		.antMatchers("/users/login").permitAll()
		.antMatchers("/admin/**").hasRole(UserDomainConstants.ROLE_ADMIN)
		.anyRequest().authenticated();
	}
	
}

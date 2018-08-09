package com.thalasoft.user.rest.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.thalasoft.toolbox.condition.EnvProd;
import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.AuthenticationFromCredentialsFilter;
import com.thalasoft.user.rest.security.AuthenticationFromTokenFilter;
import com.thalasoft.user.rest.security.CustomAuthenticationProvider;
import com.thalasoft.user.rest.security.NoRedirectStrategy;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.UserDomainConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

@EnvProd
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( // TODO What is this for ?
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
// @EnableGlobalMethodSecurity(prePostEnabled=true) TODO see what it is used
// @EnableGlobalMethodSecurity(securedEnabled = true) TODO see what it is used for
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.security",
"com.thalasoft.user.rest.service", "com.thalasoft.user.rest.filter" })
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;
	
	@Autowired
	private SimpleCORSFilter simpleCORSFilter;
	
	@Autowired
	AuthenticationFromTokenFilter authenticationFromTokenFilter;

	@Bean
    @Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter() throws Exception {
		AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter = new AuthenticationFromCredentialsFilter();
		authenticationFromCredentialsFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFromCredentialsFilter;
	}
	
	@Bean
	public AuthenticationFromTokenFilter authenticationFromTokenFilter() throws Exception {
		AuthenticationFromTokenFilter authenticationFromTokenFilter = new AuthenticationFromTokenFilter(new NegatedRequestMatcher(new AntPathRequestMatcher("/users/login", RequestMethod.POST.name())));
		authenticationFromTokenFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFromTokenFilter;
	}

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Autowired
    private UserDetailsService userDetailsService;

	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
		authenticationManagerBuilder.userDetailsService(userDetailsService);
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

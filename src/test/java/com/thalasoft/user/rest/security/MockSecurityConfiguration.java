package com.thalasoft.user.rest.security;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.UserDomainConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.security" })
public class MockSecurityConfiguration extends WebSecurityConfigurerAdapter {

	protected final String USER = "stephane";
	protected final String PASSWORD = "mypassword";

    @Autowired
    private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(USER).password(PASSWORD).roles("ADMIN");
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.formLogin().disable()
		.httpBasic().disable()
		.logout().disable();

		http
		.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint);

		http
		.authorizeRequests()
		.antMatchers("/", "/error").permitAll()
		.antMatchers("/users/login").permitAll()
		.antMatchers("/admin/**").hasRole(UserDomainConstants.ROLE_ADMIN)
		.anyRequest().authenticated();
	}
	
    @Bean
    public SimpleCORSFilter simpleCORSFilter() throws Exception {
        return new SimpleCORSFilter();
    }
	
	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}

}

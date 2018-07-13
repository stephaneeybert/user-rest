package com.thalasoft.user.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.UserDomainConstants;

@Configuration
@EnableWebSecurity
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.security" })
public class WebSecurityTestConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("stephane").password("mypassword").roles("ADMIN");		
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http
		.csrf().disable()
		.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
		.and().authorizeRequests()
		.antMatchers(RESTConstants.SLASH + RESTConstants.API + RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + UserDomainConstants.LOGIN).permitAll()
		.antMatchers(RESTConstants.SLASH + RESTConstants.API + "/**").hasRole("ADMIN").anyRequest().authenticated()
		.anyRequest().authenticated();
	}
	
    @Bean
    public SimpleCORSFilter simpleCORSFilter() throws Exception {
        return new SimpleCORSFilter();
    }
    
}

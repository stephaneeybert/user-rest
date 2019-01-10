package com.thalasoft.user.rest.security.oauth2;

import java.util.Arrays;
import java.util.List;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.filter.AuthenticationFromCredentialsFilter;
import com.thalasoft.user.rest.security.filter.AuthenticationFromTokenFilter;
import com.thalasoft.user.rest.security.PathRequestMatcher;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.security.service.UserDetailsServiceImpl;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

@EnableWebSecurity
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.service",
    "com.thalasoft.user.rest.filter" })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder userPasswordEncoder;

  // @Bean
  // public UserDetailsService userDetailsService() {
  //   return new UserDetailsServiceImpl();
  // }

  // We can’t inject directly the AuthenticationManager bean anymore in
  // Spring-Boot 2.0, but it still is required by Spring Security and so we need
  // to implement a small hack in order to gain access to this object
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(userPasswordEncoder);
    return authProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider());
  }

  // Allow preflight requests
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
  }

}

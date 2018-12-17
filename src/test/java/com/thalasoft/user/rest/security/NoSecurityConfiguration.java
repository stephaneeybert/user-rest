package com.thalasoft.user.rest.security;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@EnableWebSecurity
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.filter" })
public class NoSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private SimpleCORSFilter simpleCORSFilter;

  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors();

    http.csrf().disable().formLogin().disable().httpBasic().disable().logout().disable();

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(simpleCORSFilter, ChannelProcessingFilter.class);
  }

}

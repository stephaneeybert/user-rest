package com.thalasoft.user.rest.security;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.oauth2.ResourceServerConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.security" })
public class NoSecurityConfiguration extends ResourceServerConfiguration {

  @Autowired
  private SimpleCORSFilter simpleCORSFilter;

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.cors();

    http.csrf().disable().formLogin().disable().httpBasic().disable().logout().disable();

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(simpleCORSFilter, ChannelProcessingFilter.class);
  }

}

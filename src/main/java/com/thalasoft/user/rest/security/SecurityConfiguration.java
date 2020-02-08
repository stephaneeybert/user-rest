package com.thalasoft.user.rest.security;

import java.util.Arrays;
import java.util.List;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;
import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.security.AuthenticationFromCredentialsFilter;
import com.thalasoft.user.rest.security.AuthenticationFromTokenFilter;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

@EnableWebSecurity
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.service",
    "com.thalasoft.user.rest.filter" })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

  @Autowired
  private SimpleCORSFilter simpleCORSFilter;

  @Autowired
  private AuthenticationFromTokenFilter authenticationFromTokenFilter;

  // We can’t inject directly the AuthenticationManager bean anymore in
  // Spring-Boot 2.0,
  // but it still is required by Spring Security and so we need to implement a
  // small
  // hack in order to gain access to this object
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter() throws Exception {
    AuthenticationFromCredentialsFilter authenticationFromCredentialsFilter = new AuthenticationFromCredentialsFilter(
        new AntPathRequestMatcher(
            RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.LOGIN,
            RequestMethod.POST.name()));
    authenticationFromCredentialsFilter.setAuthenticationManager(authenticationManagerBean());
    return authenticationFromCredentialsFilter;
  }

  @Bean
  public AuthenticationFromTokenFilter authenticationFromTokenFilter() throws Exception {
    AuthenticationFromTokenFilter authenticationFromTokenFilter = new AuthenticationFromTokenFilter(
        securedPathRequestMatcher());
    authenticationFromTokenFilter.setAuthenticationManager(authenticationManagerBean());
    return authenticationFromTokenFilter;
  }

  @Bean
  FilterRegistrationBean<AuthenticationFromTokenFilter> disableAutoRegistration(
      final AuthenticationFromTokenFilter filter) {
    final FilterRegistrationBean<AuthenticationFromTokenFilter> registration = new FilterRegistrationBean<AuthenticationFromTokenFilter>(
        filter);
    registration.setEnabled(false);
    return registration;
  }

  private PathRequestMatcher securedPathRequestMatcher() throws Exception {
    return new PathRequestMatcher(getUnsecuredPaths(), "/**");
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

    http.csrf().disable() // CSRF protection is disabled as cookies are not being used
        .formLogin().disable().httpBasic().disable().logout().disable();

    http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(simpleCORSFilter, ChannelProcessingFilter.class);

    http.addFilterBefore(authenticationFromTokenFilter, UsernamePasswordAuthenticationFilter.class).authorizeRequests()
        .antMatchers(getUnsecuredPaths().toArray(new String[] {})).permitAll()
        .antMatchers(RESTConstants.SLASH + DomainConstants.ADMINS + "/**").hasRole(DomainConstants.ROLE_ADMIN)
        .anyRequest().authenticated();
  }

  private List<String> getUnsecuredPaths() {
    List<String> unsecuredPaths = Arrays.asList(RESTConstants.SLASH,
        RESTConstants.SLASH + DomainConstants.ERROR + "/**", RESTConstants.SLASH + DomainConstants.ACTUATOR + "/**",
        RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.LOGIN,
        RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.TOKEN_REFRESH);
    return unsecuredPaths;
  }

}

package com.thalasoft.user.rest.security.oauth2;

import java.util.Arrays;
import java.util.List;

import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.properties.JwtProperties;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
  private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";

  @Autowired
  private JwtProperties jwtProperties;

  @Autowired
  private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

  @Autowired
  private SimpleCORSFilter simpleCORSFilter;

  @Autowired
  private DefaultTokenServices defaultTokenServices;

  @Override
  public void configure(ResourceServerSecurityConfigurer configurer) {
    configurer.resourceId(AuthorizationServerConfiguration.RESOURCE_SERVER_ID);
    configurer.tokenServices(defaultTokenServices);
  }

  // // Start duplicate code of token store
  // @Bean
	// public TokenStore resourceServerTokenStore() {
	// 	return new JwtTokenStore(resourceServerJwtAccessTokenConverter());
  // }
  
  // @Bean
	// public JwtAccessTokenConverter resourceServerJwtAccessTokenConverter() {
	// 	JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
	// 	jwtAccessTokenConverter.setKeyPair(new KeyStoreKeyFactory(new ClassPathResource(jwtProperties.getSslKeystoreFilename()), jwtProperties.getSslKeystorePassword().toCharArray()).getKeyPair(jwtProperties.getSslKeyPair()));
	// 	return jwtAccessTokenConverter;
  // }
  
  // @Bean
	// @Primary
	// public DefaultTokenServices resourceServerDefaultTokenServices() {
	// 	DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
  //   defaultTokenServices.setTokenStore(resourceServerTokenStore());
  //   // defaultTokenServices.setClientDetailsService(clientDetailsService);
	// 	defaultTokenServices.setSupportRefreshToken(true);
	// 	return defaultTokenServices;
	// }
  // // End duplicate code of token store

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // TODO For now allow non https access as the browser non signed certificate
    // warning sucks
    // Maybe someday we'll have a signed certificate...
    // http.requiresChannel().antMatchers(RESTConstants.SLASH + RESTConstants.API +
    // "/**").requiresSecure();

    // http.headers().cacheControl(); TODO what is this used for ?
    // http.headers().cacheControl().disable().frameOptions().disable();

    http.cors();

    http.csrf().disable() // CSRF protection is disabled as cookies are not being used
        // .formLogin().disable() TODO
        // .httpBasic().disable()
        // .logout().disable()
        ;

    http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(simpleCORSFilter, ChannelProcessingFilter.class);

    http
        // .requestMatchers().antMatchers(SECURED_PATTERN).and()
        .authorizeRequests().antMatchers(getUnsecuredPaths().toArray(new String[] {})).permitAll()
        .antMatchers(RESTConstants.SLASH + DomainConstants.ADMINS + "/**").hasRole(DomainConstants.ROLE_ADMIN)
        // .antMatchers(HttpMethod.POST, SECURED_PATTERN).access(SECURED_WRITE_SCOPE)
        // .anyRequest().access(SECURED_READ_SCOPE);
        .anyRequest().authenticated();
  }

  private List<String> getUnsecuredPaths() {
    List<String> unsecuredPaths = Arrays.asList(RESTConstants.SLASH,
        RESTConstants.SLASH + DomainConstants.ERROR + "/**", RESTConstants.SLASH + DomainConstants.ACTUATOR + "/**",
        // RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.AUTHORIZE,
        RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.LOGIN,
        RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.TOKEN,
        RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.TOKEN_REFRESH);
    return unsecuredPaths;
  }

}

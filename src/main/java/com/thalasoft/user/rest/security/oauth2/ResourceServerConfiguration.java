package com.thalasoft.user.rest.security.oauth2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.thalasoft.user.rest.filter.SimpleCORSFilter;
import com.thalasoft.user.rest.properties.JwtProperties;
import com.thalasoft.user.rest.security.RESTAuthenticationEntryPoint;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
  private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";
  private static final String SSL_PUBLIC_KEY_BORDER = "-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----";
  @Autowired
  private CustomAccessTokenConverter customAccessTokenConverter;

  @Autowired
  private JwtProperties jwtProperties;

  @Autowired
  private RESTAuthenticationEntryPoint restAuthenticationEntryPoint;

  @Autowired
  private SimpleCORSFilter simpleCORSFilter;

  @Override
  public void configure(ResourceServerSecurityConfigurer configurer) {
    configurer.resourceId(AuthorizationServerConfiguration.RESOURCE_SERVER_ID);
    configurer.tokenServices(resourceServerDefaultTokenServices());
  }

  public Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
    OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
    OAuth2AccessToken accessToken = resourceServerTokenStore().readAccessToken(details.getTokenValue());
    return accessToken.getAdditionalInformation();
  }

	private DefaultTokenServices resourceServerDefaultTokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(resourceServerTokenStore());
    // defaultTokenServices.setClientDetailsService(clientDetailsService);
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

  private TokenStore resourceServerTokenStore() {
    return new JwtTokenStore(jwtAccessTokenConverter());
  }

  private JwtAccessTokenConverter jwtAccessTokenConverter() {
    JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();

    // TODO Instead of having the public key in a resource file, try obtaining it from
    // the authorization server from the /oauth/token_key endpoint
    // See https://github.com/spring-projects/spring-security-oauth/blob/master/docs/oauth2.md#jwt-tokens
    String publicKey = null;
    try {
      File resource = new ClassPathResource(jwtProperties.getSslPublicKeyFilename()).getFile();
      publicKey = new String(Files.readAllBytes(resource.toPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String verifierKey = String.format(SSL_PUBLIC_KEY_BORDER, publicKey);
    jwtAccessTokenConverter.setVerifierKey(verifierKey);

    jwtAccessTokenConverter.setAccessTokenConverter(customAccessTokenConverter);

    return jwtAccessTokenConverter;
  }

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

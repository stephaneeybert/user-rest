package com.thalasoft.user.rest.security.oauth2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.properties.JwtProperties;
import com.thalasoft.user.rest.security.service.TokenAuthenticationService;
import com.thalasoft.user.rest.utils.CommonConstants;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	public static final String OAUTH_CLIENT_ID = "ng-zero";
	public static final String OAUTH_CLIENT_SECRET = "alittlesecret";
	private static final String OAUTH_CLIENT_URL = "http://localhost:4200/callback";
	public static final String OAUTH_GRANT_TYPE_PASSWORD = "password";
	private static final String OAUTH_GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	private static final String OAUTH_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
	public static final String RESOURCE_SERVER_ID = "resources-rest";

	@Autowired
	private PasswordEncoder oauthClientPasswordEncoder;

	@Autowired
  private JwtProperties jwtProperties;

	@Autowired
	private UserDetailsService userDetailsService;

  // @Autowired
  // private ClientDetailsService clientDetailsService;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
  @Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;
  
  @Bean
  public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
    return new OAuth2AccessDeniedHandler();
  }
  
	// Define the client applications
	// TODO The client applications should be defined in a database instead of in memory
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		// The client id and client secret
		.withClient(OAUTH_CLIENT_ID)
		.secret(oauthClientPasswordEncoder.encode(OAUTH_CLIENT_SECRET))
		// The endpoint at the client application to redirect to
		.redirectUris(OAUTH_CLIENT_URL)
		// The type of request the authorization server expects for the client
		.authorizedGrantTypes(OAUTH_GRANT_TYPE_PASSWORD, OAUTH_GRANT_TYPE_AUTHORIZATION_CODE, OAUTH_GRANT_TYPE_REFRESH_TOKEN)
		// The permissions the client needs to send requests to the authorization server
		.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
		// The resources server id
		.resourceIds(RESOURCE_SERVER_ID)
		// The scope of content offered by the resources servers
		.scopes("read_profile", "write_profile", "read_firstname")
		// The lifespan of the tokens for the client application
		.accessTokenValiditySeconds(jwtProperties.getAccessTokenExpirationTime())
		.refreshTokenValiditySeconds(jwtProperties.getRefreshTokenExpirationTime());
	}
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    // For an asymmetric encryption, that is, a certificate with a private key and a public key,
    // the resources server needs access to the public key so as to encrypt the JWT token
    // before sending a request to the authorization server which then uses its private key
    // to decrypt the token received in the request
    // The resources server can get the public key from the /oauth/token_key endpoint
		// And Spring Security OAuth can expose two endpoints for checking tokens:
		// /oauth/check_token and /oauth/token_key
		// Those endpoints are not exposed by default as they have access denyAll()
		// To enable them and verify the tokens with these endpoints, add the following configuration:
		security
		.tokenKeyAccess("permitAll()")
		.checkTokenAccess("isAuthenticated()")
    .passwordEncoder(oauthClientPasswordEncoder);
		// .allowFormAuthenticationForClients();
		// oauthServer
		// Allow a client application to request a token and to verify a token only if the client application has the ROLE_TRUSTED_CLIENT authority
		// .tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
		// .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
		.authenticationManager(authenticationManager)
    .tokenServices(defaultTokenServices())
    .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
    .tokenEnhancer(jwtAccessTokenConverter())
    .accessTokenConverter(jwtAccessTokenConverter())
		.userDetailsService(userDetailsService);

		// The URL paths provided by the framework are:
		// /oauth/authorize (the authorization endpoint)
		// /oauth/token (the token endpoint)
		// /oauth/confirm_access (user posts approval for grants here)
		// /oauth/error (used to render errors in the authorization server)
		// /oauth/check_token (used by Resource Servers to decode access tokens)
		// /oauth/token_key (exposes public key for token verification if using JWT tokens)
		endpoints
		// .pathMapping("/oauth/authorize", RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.AUTHORIZE)
		.pathMapping("/oauth/token", RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.TOKEN);
		
		// if (jwtProperties.getCheckUserScopes()) {
		// 	endpoints.requestFactory(requestFactory());
    // }
	}

  // @Bean
  // public JwtAccessTokenConverter jwtAccessTokenConverter() {
  //   JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
  //   tokenConverter.setSigningKey(PRIVATE_KEY);
  //   tokenConverter.setVerifierKey(PUBLIC_KEY);
  //   return tokenConverter;
  // }

  @Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setKeyPair(new KeyStoreKeyFactory(new ClassPathResource(jwtProperties.getSslKeystoreFilename()), jwtProperties.getSslKeystorePassword().toCharArray()).getKeyPair(jwtProperties.getSslKeyPair()));
		return jwtAccessTokenConverter;
	}

  @Bean
  public TokenEnhancer tokenEnhancer() {
      return new CustomTokenEnhancer();
  }

	// Add user information to the token
	class CustomTokenEnhancer implements TokenEnhancer {

		@Override
		public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
			User user = (User) authentication.getPrincipal();
			Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
      info.put(CommonConstants.JWT_CLAIM_USER_EMAIL, user.getEmail().getEmailAddress());
			info.put(CommonConstants.JWT_CLAIM_USER_FULLNAME, user.getFirstname() + " " + user.getLastname());
      info.put("scopes", authentication.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
      info.put("organization", authentication.getName());
			DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
      customAccessToken.setAdditionalInformation(info);
      customAccessToken.setExpiration(tokenAuthenticationService.getExpirationDate());
			return customAccessToken;
    }
    
	}
			
	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	@Bean
	@Primary
	public DefaultTokenServices defaultTokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(tokenStore());
    // defaultTokenServices.setClientDetailsService(clientDetailsService);
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

}

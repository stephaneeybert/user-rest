package com.thalasoft.user.rest.security.oauth2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.security.service.TokenAuthenticationService;
import com.thalasoft.user.rest.utils.CommonConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

class CustomTokenEnhancer implements TokenEnhancer {

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

  // Add user information to the token
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
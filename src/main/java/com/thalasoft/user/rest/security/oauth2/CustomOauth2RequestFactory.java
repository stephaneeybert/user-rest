package com.thalasoft.user.rest.security.oauth2;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

// There is a chance of having stale user information in the token claims. when receiving a refresh token request. Because the refresh token has a long life span, the user information it carries in its claims might be stale compared to the user in the database. Therefore, when receivig a refrehs token request, prefer getting the user from the database instead of from the token claims.

@Configuration
class CustomOauth2RequestFactory extends DefaultOAuth2RequestFactory {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
	private UserDetailsService userDetailsService;

    public CustomOauth2RequestFactory(ClientDetailsService clientDetailsService) {
        super(clientDetailsService);
    }

    @Override
    public TokenRequest createTokenRequest(Map<String, String> requestParameters, ClientDetails authenticatedClient) {
        if (requestParameters.get("grant_type").equals("refresh_token")) {
            OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(
                    tokenStore.readRefreshToken(requestParameters.get("refresh_token")));
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(authentication.getName(), null,
                            userDetailsService.loadUserByUsername(authentication.getName()).getAuthorities()));
        }
        return super.createTokenRequest(requestParameters, authenticatedClient);
    }

}

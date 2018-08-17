package com.thalasoft.user.rest.properties;

public interface JwtProperties {

    public String getTokenIssuer();

    public String getTokenPrivateKey();

    public Integer getAccessTokenExpirationTime();

    public Integer getRefreshTokenExpirationTime();
    
}

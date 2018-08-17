package com.thalasoft.user.rest.properties;

public abstract class AbstractJwtProperties implements JwtProperties {

    private String tokenIssuer;
    private String tokenPrivateKey;
    private Integer accessTokenExpirationTime;
    private Integer refreshTokenExpirationTime;
    
    public String getTokenIssuer() {
        return tokenIssuer;
    }

    public void setTokenIssuer(String tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    public String getTokenPrivateKey() {
        return tokenPrivateKey;
    }

    public void setTokenPrivateKey(String tokenPrivateKey) {
        this.tokenPrivateKey = tokenPrivateKey;
    }

    public Integer getAccessTokenExpirationTime() {
        return accessTokenExpirationTime;
    }

    public void setAccessTokenExpirationTime(Integer accessTokenExpirationTime) {
        this.accessTokenExpirationTime = accessTokenExpirationTime;
    }

    public Integer getRefreshTokenExpirationTime() {
        return refreshTokenExpirationTime;
    }

    public void setRefreshTokenExpirationTime(Integer refreshTokenExpirationTime) {
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
    }

}

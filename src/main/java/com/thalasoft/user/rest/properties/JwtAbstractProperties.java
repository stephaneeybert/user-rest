package com.thalasoft.user.rest.properties;

public abstract class JwtAbstractProperties implements JwtProperties {

  private String tokenIssuer;
  private String tokenPrivateKey;
  private Integer accessTokenExpirationTime;
  private Integer refreshTokenExpirationTime;
  private Integer allowedClockSkewSeconds;
  private Boolean checkUserScopes;
  private String sslKeystoreFilename;
  private String sslKeystorePassword;
  private String sslKeyPair;

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

  public Integer getAllowedClockSkewSeconds() {
    return allowedClockSkewSeconds;
  }

  public void setAllowedClockSkewSeconds(Integer allowedClockSkewSeconds) {
    this.allowedClockSkewSeconds = allowedClockSkewSeconds;
  }

  public Boolean getCheckUserScopes() {
    return this.checkUserScopes;
  }

  public void setCheckUserScopes(Boolean checkUserScopes) {
    this.checkUserScopes = checkUserScopes;
  }

  public String getSslKeystoreFilename() {
    return this.sslKeystoreFilename;
  }

  public void setSslKeystoreFilename(String sslKeystoreFilename) {
    this.sslKeystoreFilename = sslKeystoreFilename;
  }

  public String getSslKeystorePassword() {
    return this.sslKeystorePassword;
  }

  public void setSslKeystorePassword(String sslKeystorePassword) {
    this.sslKeystorePassword = sslKeystorePassword;
  }

  public String getSslKeyPair() {
    return this.sslKeyPair;
  }

  public void setSslKeyPair(String sslKeyPair) {
    this.sslKeyPair = sslKeyPair;
  }

}

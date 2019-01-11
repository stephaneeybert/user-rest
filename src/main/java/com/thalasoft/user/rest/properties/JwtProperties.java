package com.thalasoft.user.rest.properties;

public interface JwtProperties {

    public String getTokenIssuer();

    public String getTokenPrivateKey();

    public Integer getAccessTokenExpirationTime();

    public Integer getRefreshTokenExpirationTime();
 
    public Integer getAllowedClockSkewSeconds();
    
    public Boolean getCheckUserScopes();

    public String getSslKeystoreFilename();

    public String getSslKeystorePassword();

    public String getSslKeyPair();
    
    public String getSslPublicKeyFilename();
    
}

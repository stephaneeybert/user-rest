package com.thalasoft.user.rest.properties;

public interface ApplicationProperties {

    public String getAuthenticationTokenPrivateKey();

    public String getHost();

    public String getPort();

    public String getMailFrom();

    public String getPassword();

    public String getProtocol();

    public String getUsername();

    public boolean getMailTestConnection();

    public boolean getMailingEnabled();
    
}

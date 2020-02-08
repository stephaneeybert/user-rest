package com.thalasoft.user.rest.properties;

import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractUserProperties implements ApplicationProperties {

  @Value("${" + PropertyNames.CONFIG_MAIL_HOST + "}")
  private String host;

  @Value("${" + PropertyNames.CONFIG_MAIL_PORT + "}")
  private String port;

  @Value("${" + PropertyNames.CONFIG_MAIL_PROTOCOL + "}")
  private String protocol;

  @Value("${" + PropertyNames.CONFIG_MAIL_USERNAME + "}")
  private String username;

  @Value("${" + PropertyNames.CONFIG_MAIL_PASSWORD + "}")
  private String password;

  @Value("${" + PropertyNames.CONFIG_MAIL_FROM + "}")
  private String mailFrom;

  @Value("${" + PropertyNames.CONFIG_MAIL_TEST_CONNECTION + "}")
  private boolean mailTestConnection;

  @Value("${" + PropertyNames.CONFIG_MAILING_ENABLED + "}")
  private boolean mailingEnabled;

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public String getPort() {
    return port;
  }

  @Override
  public String getProtocol() {
    return protocol;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getMailFrom() {
    return mailFrom;
  }

  @Override
  public boolean getMailTestConnection() {
    return mailTestConnection;
  }

  @Override
  public boolean getMailingEnabled() {
    return mailingEnabled;
  }

}

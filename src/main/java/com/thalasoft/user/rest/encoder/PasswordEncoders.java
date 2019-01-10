package com.thalasoft.user.rest.encoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoders {

  private static final int ENCODING_ROUNDS_OAUTH_CLIENT = 4;
  private static final int ENCODING_ROUNDS_OAUTH_USER = 8;

  @Bean
  public PasswordEncoder oauthClientPasswordEncoder() {
    return new BCryptPasswordEncoder(ENCODING_ROUNDS_OAUTH_CLIENT);
  }

  @Bean
  public PasswordEncoder userPasswordEncoder() {
    return new BCryptPasswordEncoder(ENCODING_ROUNDS_OAUTH_USER);
  }
  
}

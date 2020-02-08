package com.thalasoft.user.rest.properties;

import com.thalasoft.toolbox.condition.EnvTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@EnvTest
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtTestProperties extends JwtAbstractProperties {

  private static Logger logger = LoggerFactory.getLogger(JwtTestProperties.class);

  public JwtTestProperties() {
    logger.debug("Loading the JWT test properties file");
  }

}

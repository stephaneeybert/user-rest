package com.thalasoft.user.rest.properties;

import com.thalasoft.toolbox.condition.EnvProd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@EnvProd
@Component
@PropertySource({ "classpath:user-prod.properties" })
public class UserProdProperties extends AbstractUserProperties {

  private static Logger logger = LoggerFactory.getLogger(UserProdProperties.class);

  public UserProdProperties() {
    logger.debug("Loading the Prod properties file");
  }

}

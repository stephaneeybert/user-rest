package com.thalasoft.user.rest.properties;

import com.thalasoft.toolbox.condition.EnvPreProd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@EnvPreProd
@Component
@PropertySource({ "classpath:user-preprod.properties" })
public class UserPreProdProperties extends AbstractUserProperties {

  private static Logger logger = LoggerFactory.getLogger(UserPreProdProperties.class);

  public UserPreProdProperties() {
    logger.debug("Loading the Preprod properties file");
  }

}

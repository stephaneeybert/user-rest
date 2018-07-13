package com.thalasoft.user.rest.properties;

import com.thalasoft.toolbox.condition.EnvAcceptance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@EnvAcceptance
@Component
@PropertySource({ "classpath:user-acceptance.properties" })
public class UserAcceptanceProperties extends AbstractUserProperties {

	private static Logger logger = LoggerFactory.getLogger(UserAcceptanceProperties.class);

	public UserAcceptanceProperties() {
		logger.debug("Loading the Acceptance properties file");
	}

}

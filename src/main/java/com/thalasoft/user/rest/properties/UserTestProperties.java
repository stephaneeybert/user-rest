package com.thalasoft.user.rest.properties;

import com.thalasoft.toolbox.condition.EnvTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@EnvTest
@Component
@PropertySource({ "classpath:user-test.properties" })
public class UserTestProperties extends AbstractUserProperties {
	
    private static Logger logger = LoggerFactory.getLogger(UserTestProperties.class);

    public UserTestProperties() {
        logger.debug("Loading the application test properties file");
    }

}

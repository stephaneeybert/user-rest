package com.thalasoft.user.rest.properties;

import com.thalasoft.toolbox.condition.EnvProd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@EnvProd
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProdProperties extends AbstractJwtProperties {
	
    private static Logger logger = LoggerFactory.getLogger(JwtProdProperties.class);

    public JwtProdProperties() {
        logger.debug("Loading the JWT prod properties file");
    }

}

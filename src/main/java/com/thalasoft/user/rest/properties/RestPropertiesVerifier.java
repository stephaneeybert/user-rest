package com.thalasoft.user.rest.properties;

import com.thalasoft.toolbox.spring.AbstractPropertiesVerifier;

import org.springframework.stereotype.Component;

@Component
public class RestPropertiesVerifier extends AbstractPropertiesVerifier {

    RestPropertiesVerifier() {
        super(PropertyNames.class);
    }

}

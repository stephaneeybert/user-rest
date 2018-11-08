package com.thalasoft.user.rest.ut;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.thalasoft.user.rest.resource.UserResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private Validator validator;

    private UserResource userResource;

    private Set<ConstraintViolation<UserResource>> constraintViolations;

    @Before
    public void beforeAnyTest() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        userResource = new UserResource();
        userResource.setFirstname("Marc");
        userResource.setLastname("Dupuis");
        userResource.setEmail("marc@yahoo.fr");
        userResource.setConfirmedEmail(true);
    }

    @After
    public void afterAnyTest() {
    }

    @Test
    public void testNoValidationViolation() {
        constraintViolations = validator.validate(userResource);
        assertThat(constraintViolations.size(), is(0));
    }

    @Test
    public void testEmptyEmailViolation() {
        userResource.setEmail(null);
        constraintViolations = validator.validate(userResource);
        assertThat(constraintViolations.size(), is(1));
    }

    @Test
    public void testEmptyNameViolation() {
        userResource.setFirstname(null);
        userResource.setLastname(null);
        constraintViolations = validator.validate(userResource);
        assertThat(constraintViolations.size(), is(2));
    }

}

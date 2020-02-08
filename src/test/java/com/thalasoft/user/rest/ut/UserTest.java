package com.thalasoft.user.rest.ut;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.thalasoft.user.rest.resource.UserModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

  private Validator validator;

  private UserModel userModel;

  private Set<ConstraintViolation<UserModel>> constraintViolations;

  @Before
  public void beforeAnyTest() throws Exception {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    userModel = new UserModel();
    userModel.setFirstname("Marc");
    userModel.setLastname("Dupuis");
    userModel.setEmail("marc@yahoo.fr");
    userModel.setConfirmedEmail(true);
  }

  @After
  public void afterAnyTest() {
  }

  @Test
  public void testNoValidationViolation() {
    constraintViolations = validator.validate(userModel);
    assertThat(constraintViolations.size(), is(0));
  }

  @Test
  public void testEmptyEmailViolation() {
    userModel.setEmail(null);
    constraintViolations = validator.validate(userModel);
    assertThat(constraintViolations.size(), is(1));
  }

  @Test
  public void testEmptyNameViolation() {
    userModel.setFirstname(null);
    userModel.setLastname(null);
    constraintViolations = validator.validate(userModel);
    assertThat(constraintViolations.size(), is(2));
  }

}

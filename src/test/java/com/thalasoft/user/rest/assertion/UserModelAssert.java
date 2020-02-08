package com.thalasoft.user.rest.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.resource.UserRoleModel;

import org.assertj.core.api.AbstractAssert;

public class UserModelAssert extends AbstractAssert<UserModelAssert, UserModel> {

  private UserModelAssert(UserModel actual) {
    super(actual, UserModelAssert.class);
  }

  public static UserModelAssert assertThatUserModel(UserModel actual) {
    return new UserModelAssert(actual);
  }

  public UserModelAssert hasId(Long id) {
    isNotNull();
    assertThat(actual.getModelId())
        .overridingErrorMessage("Expected the id to be <%s> but was <%s>.", id, actual.getModelId()).isEqualTo(id);
    return this;
  }

  public UserModelAssert hasEmail(String email) {
    isNotNull();
    assertThat(actual.getEmail().toString())
        .overridingErrorMessage("Expected the email to be <%s> but was <%s>.", email, actual.getEmail())
        .isEqualTo(email.toString());
    return this;
  }

  public UserModelAssert isEmailConfirmed() {
    isNotNull();
    assertThat(actual.isConfirmedEmail()).overridingErrorMessage("Expected the email to be confirmed but was not.")
        .isTrue();
    return this;
  }

  public UserModelAssert hasPassword(String password) {
    isNotNull();
    assertThat(actual.getPassword())
        .overridingErrorMessage("Expected the password to be <%s> but was <%s>.", password, actual.getPassword())
        .isEqualTo(password);
    return this;
  }

  public UserModelAssert isSameAs(UserModel user) {
    isNotNull();
    assertThat(actual.hashCode())
        .overridingErrorMessage("Expected the hash code to be <%s> but was <%s>.", user.hashCode(), actual.hashCode())
        .isEqualTo(user.hashCode());
    return this;
  }

  public UserModelAssert hasFirstname(String firstname) {
    isNotNull();
    assertThat(actual.getFirstname())
        .overridingErrorMessage("Expected the firstname to be <%s> but was <%s>.", firstname, actual.getFirstname())
        .isEqualTo(firstname);
    return this;
  }

  public UserModelAssert hasLastname(String lastname) {
    isNotNull();
    assertThat(actual.getLastname())
        .overridingErrorMessage("Expected the lastname to be <%s> but was <%s>.", lastname, actual.getLastname())
        .isEqualTo(lastname);
    return this;
  }

  public UserModelAssert exists() {
    isNotNull();
    assertThat(actual).overridingErrorMessage("Expected the user to exist but it didn't.").isNotNull();
    return this;
  }

  public UserModelAssert doesNotExist() {
    isNull();
    assertThat(actual).overridingErrorMessage("Expected the user not to exist but it did.").isNull();
    return this;
  }

  public UserModelAssert hasRole(String role) {
    isNotNull();
    boolean hasRole = false;
    for (UserRoleModel userRoleModel : actual.getUserRoleModels()) {
      if (userRoleModel.getRole().equals(role)) {
        hasRole = true;
      }
    }
    assertThat(hasRole).overridingErrorMessage("Expected to have the role <%s> but didn't.", role).isTrue();
    return this;
  }

}

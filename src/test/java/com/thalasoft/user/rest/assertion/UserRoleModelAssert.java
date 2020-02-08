package com.thalasoft.user.rest.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import com.thalasoft.user.rest.resource.UserRoleModel;

import org.assertj.core.api.AbstractAssert;

public class UserRoleModelAssert extends AbstractAssert<UserRoleModelAssert, UserRoleModel> {

  private UserRoleModelAssert(UserRoleModel actual) {
    super(actual, UserRoleModelAssert.class);
  }

  public static UserRoleModelAssert assertThatUserModel(UserRoleModel actual) {
    return new UserRoleModelAssert(actual);
  }

  public UserRoleModelAssert hasRole(String role) {
    isNotNull();
    assertThat(actual.getRole())
        .overridingErrorMessage("Expected the email to be <%s> but was <%s>.", role, actual.getRole()).isEqualTo(role);
    return this;
  }

  public UserRoleModelAssert isSameAs(UserRoleModel userRole) {
    isNotNull();
    assertThat(actual.hashCode()).overridingErrorMessage("Expected the hash code to be <%s> but was <%s>.",
        userRole.hashCode(), actual.hashCode()).isEqualTo(userRole.hashCode());
    return this;
  }

  public UserRoleModelAssert exists() {
    isNotNull();
    assertThat(actual).overridingErrorMessage("Expected the user role to exist but it didn't.").isNotNull();
    return this;
  }

  public UserRoleModelAssert doesNotExist() {
    isNull();
    assertThat(actual).overridingErrorMessage("Expected the user role not to exist but it did.").isNull();
    return this;
  }

}

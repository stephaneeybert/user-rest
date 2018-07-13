package com.thalasoft.user.rest.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import com.thalasoft.user.rest.resource.UserRoleResource;

import org.assertj.core.api.AbstractAssert;

public class UserRoleResourceAssert extends AbstractAssert<UserRoleResourceAssert, UserRoleResource> {

	private UserRoleResourceAssert(UserRoleResource actual) {
		super(actual, UserRoleResourceAssert.class);
	}
	
	public static UserRoleResourceAssert assertThatUserResource(UserRoleResource actual) {
		return new UserRoleResourceAssert(actual);
	}

	public UserRoleResourceAssert hasRole(String role) {
		isNotNull();
		assertThat(actual.getRole()).overridingErrorMessage("Expected the email to be <%s> but was <%s>.", role, actual.getRole()).isEqualTo(role);
		return this;
	}
	
	public UserRoleResourceAssert isSameAs(UserRoleResource userRole) {
		isNotNull();
		assertThat(actual.hashCode()).overridingErrorMessage("Expected the hash code to be <%s> but was <%s>.", userRole.hashCode(), actual.hashCode()).isEqualTo(userRole.hashCode());
		return this;
	}
	
	public UserRoleResourceAssert exists() {
		isNotNull();
		assertThat(actual).overridingErrorMessage("Expected the user role to exist but it didn't.").isNotNull();
		return this;
	}
	
	public UserRoleResourceAssert doesNotExist() {
		isNull();
		assertThat(actual).overridingErrorMessage("Expected the user role not to exist but it did.").isNull();
		return this;
	}
	
}

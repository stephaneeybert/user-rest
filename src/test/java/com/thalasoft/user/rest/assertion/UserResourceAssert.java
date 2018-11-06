package com.thalasoft.user.rest.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.resource.UserRoleResource;

import org.assertj.core.api.AbstractAssert;

public class UserResourceAssert extends AbstractAssert<UserResourceAssert, UserResource> {

	private UserResourceAssert(UserResource actual) {
		super(actual, UserResourceAssert.class);
	}
	
	public static UserResourceAssert assertThatUserResource(UserResource actual) {
		return new UserResourceAssert(actual);
	}

	public UserResourceAssert hasId(Long id) {
		isNotNull();
		assertThat(actual.getResourceId()).overridingErrorMessage("Expected the id to be <%s> but was <%s>.", id, actual.getResourceId()).isEqualTo(id);
		return this;
	}
	
	public UserResourceAssert hasEmail(String email) {
		isNotNull();
		assertThat(actual.getEmail().toString()).overridingErrorMessage("Expected the email to be <%s> but was <%s>.", email, actual.getEmail()).isEqualTo(email.toString());
		return this;
	}
	
	public UserResourceAssert isEmailConfirmed() {
		isNotNull();
		assertThat(actual.isConfirmedEmail()).overridingErrorMessage("Expected the email to be confirmed but was not.").isTrue();
		return this;
	}
	
	public UserResourceAssert hasPassword(String password) {
		isNotNull();
		assertThat(actual.getPassword()).overridingErrorMessage("Expected the password to be <%s> but was <%s>.", password, actual.getPassword()).isEqualTo(password);
		return this;
	}
	
	public UserResourceAssert isSameAs(UserResource user) {
		isNotNull();
		assertThat(actual.hashCode()).overridingErrorMessage("Expected the hash code to be <%s> but was <%s>.", user.hashCode(), actual.hashCode()).isEqualTo(user.hashCode());
		return this;
	}
	
	public UserResourceAssert hasFirstname(String firstname) {
		isNotNull();
		assertThat(actual.getFirstname()).overridingErrorMessage("Expected the firstname to be <%s> but was <%s>.", firstname, actual.getFirstname()).isEqualTo(firstname);
		return this;
	}
	
	public UserResourceAssert hasLastname(String lastname) {
		isNotNull();
		assertThat(actual.getLastname()).overridingErrorMessage("Expected the lastname to be <%s> but was <%s>.", lastname, actual.getLastname()).isEqualTo(lastname);
		return this;
	}
	
	public UserResourceAssert exists() {
		isNotNull();
		assertThat(actual).overridingErrorMessage("Expected the user to exist but it didn't.").isNotNull();
		return this;
	}
	
	public UserResourceAssert doesNotExist() {
		isNull();
		assertThat(actual).overridingErrorMessage("Expected the user not to exist but it did.").isNull();
		return this;
	}
	
	public UserResourceAssert hasRole(String role) {
		isNotNull();
		boolean hasRole = false;
		for (UserRoleResource userRoleResource : actual.getUserRoleResources()) {
			if (userRoleResource.getRole().equals(role)) {
				hasRole = true;
			}
		}
		assertThat(hasRole).overridingErrorMessage("Expected to have the role <%s> but didn't.", role).isTrue();
		return this;
	}
	
}

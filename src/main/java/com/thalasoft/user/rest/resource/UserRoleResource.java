package com.thalasoft.user.rest.resource;

import org.hibernate.validator.constraints.NotEmpty;

public class UserRoleResource extends AbstractResource {

    @NotEmpty
    private String role;

    public UserRoleResource() {
    }

    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int hashCode() {
		return role.hashCode();
	}
	
}

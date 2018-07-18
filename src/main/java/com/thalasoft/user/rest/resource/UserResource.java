package com.thalasoft.user.rest.resource;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UserResource extends AbstractResource {

    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @NotEmpty
    @Email
    private String email;
    private boolean confirmedEmail;
    private String password;
    private String workPhone;
    @Valid
    private Set<UserRoleResource> userRoleResources = new HashSet<UserRoleResource>();
    
    public UserResource() {
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

	public boolean isConfirmedEmail() {
		return confirmedEmail;
	}

	public void setConfirmedEmail(boolean confirmedEmail) {
		this.confirmedEmail = confirmedEmail;
	}

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public Set<UserRoleResource> getUserRoles() {
		return userRoleResources;
	}

	public void setUserRoles(Set<UserRoleResource> userRoleResources) {
		this.userRoleResources = userRoleResources;
	}

}

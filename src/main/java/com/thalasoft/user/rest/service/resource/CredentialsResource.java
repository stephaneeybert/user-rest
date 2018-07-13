package com.thalasoft.user.rest.service.resource;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.hateoas.ResourceSupport;

public class CredentialsResource extends ResourceSupport {

    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;

    public CredentialsResource() {
    }
    
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

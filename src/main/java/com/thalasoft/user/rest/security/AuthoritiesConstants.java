package com.thalasoft.user.rest.security;

public enum AuthoritiesConstants {

	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_USER("ROLE_USER"),
	ROLE_ANONYMOUS("ROLE_ANONYMOUS");
	
	private String role;
	
	private AuthoritiesConstants(String role) {
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}

}

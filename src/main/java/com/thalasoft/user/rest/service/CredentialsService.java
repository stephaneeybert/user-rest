package com.thalasoft.user.rest.service;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.service.resource.CredentialsResource;

public interface CredentialsService {

	public User findByEmail(EmailAddress email);

	public boolean checkPassword(User user, String password);
	
	public User checkPassword(CredentialsResource credentialsResource);
	
	public User updatePassword(Long id, String password);
	
}

package com.thalasoft.user.rest.service;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.User;

public interface UserActionService {

	public void sendEmailConfirmationMail(User user);
	
	public User confirmEmail(String sialToken, Long id) throws EntityNotFoundException;
	
	public boolean authenticateAction(String sialToken, String action, Long id);
	
	public String signAction(String action, Long id);
	
}

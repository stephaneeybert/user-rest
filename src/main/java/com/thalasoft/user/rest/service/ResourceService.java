package com.thalasoft.user.rest.service;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.resource.UserResource;

public interface ResourceService {

	public User toUser(UserResource userResource);

	public UserResource fromUser(User user);
	
}

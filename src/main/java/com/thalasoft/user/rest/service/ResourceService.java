package com.thalasoft.user.rest.service;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.resource.UserResource;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

public interface ResourceService {

	public User toUser(UserResource userResource);

	public UserResource fromUser(User user);
	
	public void addPageableToUri(UriComponentsBuilder uriComponentsBuilder, Pageable pageable);
	
}

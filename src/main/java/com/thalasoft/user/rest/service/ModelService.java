package com.thalasoft.user.rest.service;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.resource.UserModel;

import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

public interface ModelService {

  public User toUser(UserModel userModel);

  public UserModel fromUser(User user);

  public void addPageableToUri(UriComponentsBuilder uriComponentsBuilder, Pageable pageable);

}

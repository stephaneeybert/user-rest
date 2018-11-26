package com.thalasoft.user.rest.service;

import java.util.HashSet;
import java.util.Set;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.resource.UserRoleResource;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ResourceServiceImpl implements ResourceService {

  @Autowired
  private UserService userService;

  @Override
  public User toUser(UserResource userResource) {
    User user = null;
    if (userResource.getResourceId() == null) {
      user = new User();
    } else {
      try {
        user = userService.findById(userResource.getResourceId());
      } catch (EntityNotFoundException e) {
        user = new User();
      }
    }
    user.setFirstname(userResource.getFirstname());
    user.setLastname(userResource.getLastname());
    user.setEmail(new EmailAddress(userResource.getEmail()));
    user.setConfirmedEmail(userResource.isConfirmedEmail());
    user.setPassword(userResource.getPassword());
    user.setWorkPhone(userResource.getWorkPhone());
    for (UserRoleResource userRoleResource : userResource.getUserRoleResources()) {
      user.addRole(userRoleResource.getRole());
    }
    return user;
  }

  @Override
  public UserResource fromUser(User user) {
    UserResource userResource = new UserResource();
    userResource.setResourceId(user.getId());
    userResource.setFirstname(user.getFirstname());
    userResource.setLastname(user.getLastname());
    userResource.setEmail(user.getEmail().toString());
    userResource.setConfirmedEmail(user.isConfirmedEmail());
    userResource.setPassword(user.getPassword());
    userResource.setWorkPhone(user.getWorkPhone());
    Set<UserRoleResource> userRoleResources = new HashSet<UserRoleResource>();
    for (UserRole userRole : user.getUserRoles()) {
      UserRoleResource userRoleResource = new UserRoleResource();
      userRoleResource.setResourceId(userRole.getId());
      userRoleResource.setRole(userRole.getRole());
      userRoleResources.add(userRoleResource);
    }
    userResource.setUserRoleResources(userRoleResources);
    return userResource;
  }

  @Override
  public void addPageableToUri(UriComponentsBuilder uriComponentsBuilder, Pageable pageable) {
    uriComponentsBuilder.queryParam("page", pageable.getPageNumber()).queryParam("size", pageable.getPageSize());
    if (pageable.getSort() != null) {
      for (Sort.Order order : pageable.getSort()) {
        uriComponentsBuilder.queryParam("sort", order.getProperty())
            .queryParam(order.getProperty() + RESTConstants.PAGEABLE_SORT_SUFFIX, order.getDirection().name());
      }
    }
  }

}

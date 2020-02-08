package com.thalasoft.user.rest.service;

import java.util.HashSet;
import java.util.Set;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.resource.UserRoleModel;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ModelServiceImpl implements ModelService {

  @Autowired
  private UserService userService;

  @Override
  public User toUser(UserModel userModel) {
    User user = null;
    if (userModel.getModelId() == null) {
      user = new User();
    } else {
      try {
        user = userService.findById(userModel.getModelId());
      } catch (EntityNotFoundException e) {
        user = new User();
      }
    }
    user.setFirstname(userModel.getFirstname());
    user.setLastname(userModel.getLastname());
    user.setEmail(new EmailAddress(userModel.getEmail()));
    user.setConfirmedEmail(userModel.isConfirmedEmail());
    user.setPassword(userModel.getPassword());
    user.setWorkPhone(userModel.getWorkPhone());
    for (UserRoleModel userRoleModel : userModel.getUserRoleModels()) {
      user.addRole(userRoleModel.getRole());
    }
    return user;
  }

  @Override
  public UserModel fromUser(User user) {
    UserModel userModel = new UserModel();
    userModel.setModelId(user.getId());
    userModel.setFirstname(user.getFirstname());
    userModel.setLastname(user.getLastname());
    userModel.setEmail(user.getEmail().toString());
    userModel.setConfirmedEmail(user.isConfirmedEmail());
    userModel.setPassword(user.getPassword());
    userModel.setWorkPhone(user.getWorkPhone());
    Set<UserRoleModel> userRoleModels = new HashSet<UserRoleModel>();
    for (UserRole userRole : user.getUserRoles()) {
      UserRoleModel userRoleModel = new UserRoleModel();
      userRoleModel.setModelId(userRole.getId());
      userRoleModel.setRole(userRole.getRole());
      userRoleModels.add(userRoleModel);
    }
    userModel.setUserRoleModels(userRoleModels);
    return userModel;
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

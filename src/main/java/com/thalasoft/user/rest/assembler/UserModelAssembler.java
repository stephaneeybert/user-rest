package com.thalasoft.user.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Collection;
import java.util.Collections;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.controller.UserController;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.service.ModelService;
import com.thalasoft.user.rest.utils.DomainConstants;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserModel> {

  @Autowired
  private ModelService modelService;

  public UserModelAssembler() {
    super(UserController.class, UserModel.class);
  }

  @Override
  public UserModel toModel(User user) {
    UserModel userModel = createModelWithId(user.getId(), user);
    BeanUtils.copyProperties(modelService.fromUser(user), userModel);
    userModel.add(
        linkTo(UserController.class).slash(user.getId()).slash(DomainConstants.ROLES).withRel(DomainConstants.ROLES));
    return userModel;
  }

  @Override
  public CollectionModel<UserModel> toCollectionModel(Iterable<? extends User> users) {
    Collection<UserModel> userModels = Collections.emptyList();
    for (User user : users) {
      UserModel userModel = createModelWithId(user.getId(), user);
      BeanUtils.copyProperties(modelService.fromUser(user), userModel);
      userModel.add(
          linkTo(UserController.class).slash(user.getId()).slash(DomainConstants.ROLES).withRel(DomainConstants.ROLES));
      userModels.add(userModel);
    }
    return new CollectionModel<>(userModels);
  }

}

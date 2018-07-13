package com.thalasoft.user.rest.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.controller.UserController;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.service.ResourceService;
import com.thalasoft.user.rest.utils.UserDomainConstants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

    @Autowired 
	private ResourceService resourceService;

    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User user) {
        UserResource userResource = createResourceWithId(user.getId(), user);
        BeanUtils.copyProperties(resourceService.fromUser(user), userResource);
        userResource.add(linkTo(UserController.class).slash(user.getId()).slash(UserDomainConstants.ROLES).withRel(UserDomainConstants.ROLES));
        return userResource;
    }

    @Override
    public List<UserResource> toResources(Iterable<? extends User> users) {
        List<UserResource> userResources = new ArrayList<UserResource>();
        for (User user : users) {
        	UserResource userResource = createResourceWithId(user.getId(), user);
            BeanUtils.copyProperties(resourceService.fromUser(user), userResource);
            userResource.add(linkTo(UserController.class).slash(user.getId()).slash(UserDomainConstants.ROLES).withRel(UserDomainConstants.ROLES));            
            userResources.add(userResource);
        }
        return userResources;
    }
    
}

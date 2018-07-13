package com.thalasoft.user.rest.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.data.service.UserRoleService;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.resource.UserRoleResource;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Autowired 
	private UserService userService;
    
    @Autowired 
	private UserRoleService userRoleService;

    @Override
	public User toUser(UserResource userResource) {
        User user = null;
        if (userResource.getResourceId() != null) {
        	user = userService.findById(userResource.getResourceId());
        }
        if (user == null) {
        	user = new User();
        }
		user.setFirstname(userResource.getFirstname());
		user.setLastname(userResource.getLastname());
		user.setEmail(new EmailAddress(userResource.getEmail()));
		user.setConfirmedEmail(userResource.isConfirmedEmail());
		user.setPassword(userResource.getPassword());
		user.setWorkPhone(userResource.getWorkPhone());
		Set<UserRole> userRoles = new HashSet<UserRole>();
		for (UserRoleResource userRoleResource : userResource.getUserRoles()) {
	        UserRole userRole = null;
	        if (userRoleResource.getResourceId() != null) {
	        	userRole = userRoleService.findById(userRoleResource.getResourceId());
	        }
	        if (userRole == null) {
	        	userRole = new UserRole();
	        }
	    	userRole.setRole(userRoleResource.getRole());
	    	userRole.setUser(user);        
			userRoles.add(userRole);
		}
		user.setUserRoles(userRoles);
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
        userResource.setUserRoles(userRoleResources);
        return userResource;
    }

}

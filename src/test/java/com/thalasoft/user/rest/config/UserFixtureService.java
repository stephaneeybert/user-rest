package com.thalasoft.user.rest.config;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.data.service.UserRoleService;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.security.AuthoritiesConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFixtureService {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;

    public static final String USER_EMAIL = "mittiprovence@yahoo.se";
    public static final String USER_PASSWORD = "mignet";
    public static final String USER_ENCODED_PASSWORD = "bWl0dGlwcm92ZW5jZUB5YWhvby5zZTptaWduZXQ4ZDE5MjcyOS0zZjRiLTQ1Y2QtYmQ5Yy00MDMxYWY=";
    public static final String USER_PASSWORD_SALT = "8d192729-3f4b-45cd-bd9c-4031af";

	private User user0;
	private UserRole role0;

	public void addUser() {
        user0 = new User();
        user0.setFirstname("Stephane");
        user0.setLastname("Eybert");
        user0.setEmail(new EmailAddress(USER_EMAIL));
        user0.setPassword(USER_ENCODED_PASSWORD);
        user0.setPasswordSalt(USER_PASSWORD_SALT);
        User user = userService.findByEmail(user0.getEmail().toString());
        if (user == null) {
	        user0 = userService.add(user0);
        } else {
        	user0 = user;
        }
        
        role0 = new UserRole();
        role0.setRole(AuthoritiesConstants.ROLE_ADMIN.getRole());
        role0.setUser(user0);
        UserRole userRole = userRoleService.findByUserAndRole(role0.getUser().getId(), role0.getRole());
        if (userRole == null) {
            role0 = userRoleService.add(role0);
        } else {
            role0 = userRole;
        }
	}

    public void removeUser() {
        userService.delete(user0.getId());
    }

}

package com.thalasoft.user.rest.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.data.service.UserRoleService;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.condition.BootstrapSQL;
import com.thalasoft.user.rest.security.AuthoritiesConstants;

@Component
@BootstrapSQL
public class BootstrapSQLData implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;

	private User user0;

	private UserRole role0;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		addFixtures();
	}

	public void addFixtures() {
		addUserFixture();
	}
	
	private void addUserFixture() {
        user0 = new User();
        user0.setFirstname("Stephane");
        user0.setLastname("Eybert");
        user0.setEmail(new EmailAddress("mittiprovence@yahoo.se"));
        final String password = "bWl0dGlwcm92ZW5jZUB5YWhvby5zZTptaWduZXQ4ZDE5MjcyOS0zZjRiLTQ1Y2QtYmQ5Yy00MDMxYWY=";
        user0.setPassword(password);
        final String passwordSalt = "8d192729-3f4b-45cd-bd9c-4031af";
        user0.setPasswordSalt(passwordSalt);
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

}

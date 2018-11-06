package com.thalasoft.user.rest.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thalasoft.toolbox.condition.EnvProd;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.resource.UserRoleResource;
import com.thalasoft.user.rest.security.AuthoritiesConstants;
import com.thalasoft.user.rest.service.ResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@EnvProd
@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application started with command-line arguments: {} .\nTo kill this application, press Ctrl + C.",
                Arrays.toString(args));
        if (null != args && args.length > 0) {
        }
        // createUserResources();
    }

    private void createUserResources() throws Exception {
        UserResource userResource0 = new UserResource();
        userResource0.setFirstname("Cyril");
        userResource0.setLastname("Eybert");
        userResource0.setEmail("cyril@yahoo.es");
        Set<UserRoleResource> userRoleResources = new HashSet<UserRoleResource>();
        UserRoleResource user0AdminRoleResource = new UserRoleResource();
        user0AdminRoleResource.setRole(AuthoritiesConstants.ROLE_ADMIN.getRole());
        userRoleResources.add(user0AdminRoleResource);
        UserRoleResource user0UserRoleResource = new UserRoleResource();
        user0UserRoleResource.setRole(AuthoritiesConstants.ROLE_USER.getRole());
        userRoleResources.add(user0UserRoleResource);
        userResource0.setUserRoleResources(userRoleResources);

        List<UserResource> manyUserResources = new ArrayList<UserResource>();
        for (int i = 0; i < 30; i++) {
            String index = intToString(i + 1, 2);
            UserResource oneUserResource = new UserResource();
            oneUserResource.setFirstname("zfirstname" + index);
            oneUserResource.setLastname("zlastname" + index);
            oneUserResource.setEmail("zemail" + index + "@nokia.com");
            User createdUser = userService.add(resourceService.toUser(oneUserResource));
            oneUserResource.setResourceId(createdUser.getId());
            manyUserResources.add(oneUserResource);
        }
    }

    private String intToString(int num, int digits) {
        String output = Integer.toString(num);
        while (output.length() < digits) {
            output = "0" + output;
        }
        return output;
    }

}
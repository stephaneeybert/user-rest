package com.thalasoft.user.rest.resource;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResource extends AbstractResource {

    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @NotEmpty
    @Email
    private String email;
    private boolean confirmedEmail;
    private String password;
    private String workPhone;
    @Valid
    private Set<UserRoleResource> userRoleResources = new HashSet<UserRoleResource>();

}

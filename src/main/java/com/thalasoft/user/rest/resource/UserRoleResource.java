package com.thalasoft.user.rest.resource;

import javax.validation.constraints.NotEmpty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRoleResource extends AbstractResource {

    @NotEmpty
    private String role;

	public int hashCode() {
		return role.hashCode();
	}
	
}

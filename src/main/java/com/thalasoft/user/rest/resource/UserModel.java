package com.thalasoft.user.rest.resource;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.*;

import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Relation(collectionRelation = RESTConstants.EMBEDDED_COLLECTION_NAME_USER)
public class UserModel extends AbstractModel {

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
  private Set<UserRoleModel> userRoleModels = new HashSet<UserRoleModel>();

}

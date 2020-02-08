package com.thalasoft.user.rest.resource;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRoleModel extends AbstractModel {

  @NotEmpty
  private String role;

  public int hashCode() {
    return role.hashCode();
  }

}

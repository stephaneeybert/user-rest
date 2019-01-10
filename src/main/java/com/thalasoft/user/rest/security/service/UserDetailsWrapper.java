package com.thalasoft.user.rest.security.service;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class UserDetailsWrapper implements UserDetails {

  private User user;

  public UserDetailsWrapper(User user) {
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  @Override
  public String getUsername() {
    return user.getEmail().toString();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (UserRole userRole : user.getUserRoles()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole()));
    }
    return grantedAuthorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    // TODO user.isAccountNonExpired()
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // TODO user.isAccountNonLocked()
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // TODO user.isCredentialsNonExpired()
    return true;
  }

  @Override
  public boolean isEnabled() {
    // TODO user.isEnabled()
    return true;
  }

}

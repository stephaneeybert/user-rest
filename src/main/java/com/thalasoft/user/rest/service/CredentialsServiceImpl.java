package com.thalasoft.user.rest.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import com.thalasoft.toolbox.utils.CommonTools;
import com.thalasoft.toolbox.utils.Security;
import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.properties.ApplicationProperties;
import com.thalasoft.user.rest.service.resource.CredentialsModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class CredentialsServiceImpl implements CredentialsService {

  private static final int ADMIN_PASSWORD_SALT_LENGTH = 30;

  @Autowired
  UserService userService;

  @Autowired
  private ApplicationProperties applicationProperties;

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private SimpleMailMessage simpleMailMessage;

  @Autowired
  private MessageSource messageSource;

  @Override
  public User findByEmail(EmailAddress email) {
    return userService.findByEmail(email.toString());
  }

  @Override
  public User checkPassword(CredentialsModel credentialsModel) throws BadCredentialsException, EntityNotFoundException {
    try {
      User user = userService.findByEmail(credentialsModel.getEmail());
      if (checkPassword(user, credentialsModel.getPassword())) {
        if (user.getUserRoles() == null) {
          throw new InsufficientAuthenticationException("The user has not been granted any roles");
        }
        return user;
      } else {
        throw new BadCredentialsException(
            "The login " + credentialsModel.getEmail() + " and password could not match.");
      }
    } catch (EntityNotFoundException e) {
      throw new BadCredentialsException("The login " + credentialsModel.getEmail() + " and password could not match.");
    }
  }

  @Override
  public boolean checkPassword(User user, String givenPassword) {
    String givenEncodedPassword = encodePassword(user.getEmail().toString(), givenPassword, user.getPasswordSalt());
    return user.getPassword().equals(givenEncodedPassword);
  }

  @Override
  @Transactional
  public User updatePassword(Long id, String password) throws EntityNotFoundException {
    try {
      User foundUser = userService.findById(id);
      foundUser.setReadablePassword(password);
      String passwordSalt = generatePasswordSalt();
      foundUser.setPasswordSalt(passwordSalt);
      foundUser.setPassword(encodePassword(foundUser.getEmail().toString(), password, passwordSalt));

      if (javaMailSender != null && applicationProperties.getMailingEnabled()) {
        simpleMailMessage.setTo(foundUser.getEmail().toString());
        simpleMailMessage.setSubject(localizeErrorMessage("user.mail.update.password.subject",
            new Object[] { foundUser.getFirstname() + " " + foundUser.getLastname() }));
        simpleMailMessage.setText(localizeErrorMessage("user.mail.update.password.body",
            new Object[] { foundUser.getFirstname() + " " + foundUser.getLastname(), password }));
        try {
          javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
          System.err.println(e.getMessage());
        }
      }
      return foundUser;
    } catch (EntityNotFoundException e) {
      throw new EntityNotFoundException("The user with id: " + id + " was not found.");
    }
  }

  @Transactional
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    return authenticate(authentication.getName(), authentication.getCredentials().toString());
  }

  @Transactional
  public Authentication authenticate(CredentialsModel credentialsModel) throws AuthenticationException {
    return authenticate(credentialsModel.getEmail(), credentialsModel.getPassword());
  }

  private Authentication authenticate(String email, String password) throws AuthenticationException {
    User user = null;
    try {
      user = findByEmail(new EmailAddress(email));
      if (checkPassword(user, password)) {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>();
        if (user.getUserRoles() == null) {
          throw new InsufficientAuthenticationException("The user has not been granted any roles");
        }
        for (UserRole userRole : user.getUserRoles()) {
          grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRole()));
        }
        return new UsernamePasswordAuthenticationToken(email, password, grantedAuthorities);
      } else {
        throw new BadCredentialsException("The login " + user.getEmail() + " and password could not match.");
      }
    } catch (EntityNotFoundException e) {
      throw new BadCredentialsException("The login " + email + " and password could not match.");
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException("The login " + email + " and password could not match.");
    }
  }

  private String encodePassword(String email, String password, String passwordSalt) {
    return new String(Security.encodeBase64(email, saltPassword(password, passwordSalt)), UTF_8);
  }

  private String generatePasswordSalt() {
    return CommonTools.generateUniqueId(ADMIN_PASSWORD_SALT_LENGTH);
  }

  private String saltPassword(String password, String salt) {
    return password + salt;
  }

  private String localizeErrorMessage(String errorCode, Object args[]) {
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(errorCode, args, locale);
  }

}

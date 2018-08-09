package com.thalasoft.user.rest.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import com.thalasoft.toolbox.utils.Common;
import com.thalasoft.toolbox.utils.Security;
import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.jpa.domain.UserRole;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.exception.CannotEncodePasswordException;
import com.thalasoft.user.rest.properties.ApplicationProperties;
import com.thalasoft.user.rest.service.resource.CredentialsResource;

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
    public User checkPassword(CredentialsResource credentialsResource) throws BadCredentialsException, EntityNotFoundException {
		User user = userService.findByEmail(credentialsResource.getEmail());
		if (user != null) {
			if (checkPassword(user, credentialsResource.getPassword())) {
                if (user.getUserRoles() == null) {
                    throw new InsufficientAuthenticationException("The user has not been granted any roles");
                }
				return user;
			} else {
                throw new BadCredentialsException("The login " + credentialsResource.getEmail() + " and password could not match.");
			}
		} else {
			throw new BadCredentialsException("The login " + credentialsResource.getEmail() + " and password could not match.");
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
		User foundUser = userService.findById(id);
		if (foundUser != null) {
			foundUser.setReadablePassword(password);
			String passwordSalt = generatePasswordSalt();
			foundUser.setPasswordSalt(passwordSalt);
			foundUser.setPassword(encodePassword(foundUser.getEmail().toString(), password, passwordSalt));

			if (javaMailSender != null && applicationProperties.getMailingEnabled()) {
				simpleMailMessage.setTo(foundUser.getEmail().toString());
				simpleMailMessage.setSubject(localizeErrorMessage("user.mail.update.password.subject", new Object[] { foundUser.getFirstname() + " " + foundUser.getLastname() }));
				simpleMailMessage.setText(localizeErrorMessage("user.mail.update.password.body", new Object[] { foundUser.getFirstname() + " " + foundUser.getLastname(), password }));
				try {
					javaMailSender.send(simpleMailMessage);
				} catch (MailException e) {
					System.err.println(e.getMessage());
				}
			}

			return foundUser;
		} else {
			throw new EntityNotFoundException("The user with id: " + id + " was not found.");            	
		}
    }

	@Transactional
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return authenticate(authentication.getName(), authentication.getCredentials().toString());
	}

	@Transactional
	public Authentication authenticate(CredentialsResource credentialsResource) throws AuthenticationException {
		return authenticate(credentialsResource.getEmail(), credentialsResource.getPassword());
	}

	private Authentication authenticate(String email, String password) throws AuthenticationException {
        User user = null;
        try {
            user = findByEmail(new EmailAddress(email));
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("The login " + email + " and password could not match.");
        }
        if (user != null) {
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
        }
        throw new BadCredentialsException("The login " + email + " and password could not match.");
	}
	
	private String encodePassword(String email, String password, String passwordSalt) {
		String encodedPassword = null;
		try {
			encodedPassword = new String(Security.encodeBase64(email, saltPassword(password, passwordSalt)), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new CannotEncodePasswordException();
		}
		return encodedPassword;
	}

    private String generatePasswordSalt() {
        return Common.generateUniqueId(ADMIN_PASSWORD_SALT_LENGTH);
    }
    
    private String saltPassword(String password, String salt) {
        return password + salt;
    }

	private String localizeErrorMessage(String errorCode, Object args[]) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(errorCode, args, locale);
	}
	
}

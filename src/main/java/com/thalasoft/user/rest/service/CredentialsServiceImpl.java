package com.thalasoft.user.rest.service;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.transaction.Transactional;

import com.thalasoft.toolbox.utils.Common;
import com.thalasoft.toolbox.utils.Security;
import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.exception.CannotEncodePasswordException;
import com.thalasoft.user.rest.properties.ApplicationProperties;
import com.thalasoft.user.rest.service.resource.CredentialsResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class CredentialsServiceImpl implements CredentialsService {

    private static Logger logger = LoggerFactory.getLogger(CredentialsServiceImpl.class);

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
        logger.debug("Credentials - In User entity check password");
		User user = userService.findByEmail(credentialsResource.getEmail());
		if (user != null) {
			if (checkPassword(user, credentialsResource.getPassword())) {
				return user;
			} else {
                throw new BadCredentialsException("The user with email: " + credentialsResource.getEmail() + " and password could not match.");            	
			}
		} else {
			throw new EntityNotFoundException("The user with email: " + credentialsResource.getEmail() + " was not found.");            	
		}
    }

	@Override
	public boolean checkPassword(User user, String password) {
		String givenPassword = encodePassword(user.getEmail().toString(), password, user.getPasswordSalt());
		logger.debug("Credentials -  Password stored: " + user.getPassword() + " Password given: " + givenPassword);
		return user.getPassword().equals(givenPassword);
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

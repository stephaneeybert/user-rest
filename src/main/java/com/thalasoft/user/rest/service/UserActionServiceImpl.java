package com.thalasoft.user.rest.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.thalasoft.toolbox.utils.SIAL;
import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.properties.ApplicationProperties;
import com.thalasoft.user.rest.properties.JwtProperties;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.DomainConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserActionServiceImpl implements UserActionService {

	@Resource
	private UserService userService;

	@Autowired
    private ApplicationProperties applicationProperties;

	@Autowired
	private JwtProperties jwtProperties;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private MessageSource messageSource;
	
	private static final long ONE_WEEK = 1000 * 60 * 60 * 24 * 7;

	@Override
	public void sendEmailConfirmationMail(User user) {	
    	String to = user.getEmail().toString();
		String from = applicationProperties.getMailFrom();
		String subject = localizeErrorMessage("user.mail.email.confirm.subject", new Object[] { user.getFirstname() + " " + user.getLastname() });
		String sialToken = signAction(DomainConstants.CONFIRM_EMAIL, user.getId());
		String url = RESTConstants.SLASH + user.getId() + RESTConstants.SLASH + DomainConstants.CONFIRM_EMAIL + RESTConstants.SLASH + sialToken;
		String body = localizeErrorMessage("user.mail.email.confirm.body", new Object[] { user.getFirstname() + " " + user.getLastname() })
		  + " <a href='" + url + "'>" + localizeErrorMessage("user.mail.email.confirm.link", null) + "</a>"
		  + localizeErrorMessage("user.mail.email.confirm.url", new Object[] { url });
		sendMail(to, from, subject, body, true, null);
	}

	private void sendMail(String to, String from, String subject, String body, boolean isHtml, List<String> filenames) {
		if (javaMailSender != null && applicationProperties.getMailingEnabled()) {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			try {
				MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
				mimeMessageHelper.setFrom(from);
				mimeMessageHelper.setTo(to);
				mimeMessageHelper.setSubject(subject);
				mimeMessageHelper.setText(body, isHtml);
				if (filenames != null) {
					for (String filename : filenames) {
						FileSystemResource file = new FileSystemResource(filename);					
						mimeMessageHelper.addAttachment(file.getFilename(), file);
					}
				}
			} catch (MessagingException e) {
				throw new MailParseException(e);
			}
        	javaMailSender.send(mimeMessage);
        }
	}
	
	private void sendHtmlMail(final String to, final String from, final String subject, final String body, final boolean isHtml, final List<String> filenames) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
                mimeMessageHelper.setTo(to);
                mimeMessageHelper.setFrom(from);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(body, isHtml);
    			if (filenames != null) {
    				for (String filename : filenames) {
    					FileSystemResource file = new FileSystemResource(filename);					
    					mimeMessageHelper.addAttachment(file.getFilename(), file);
    				}
    			}
    		}
        };
        if (javaMailSender != null && applicationProperties.getMailingEnabled()) {
        	javaMailSender.send(preparator);
        }
    }
	
	@Modifying
	@Transactional(rollbackFor = EntityNotFoundException.class)
	@Override
	public User confirmEmail(String sialToken, Long id) throws EntityNotFoundException {
		User user = userService.findById(id);
        if (user == null) {
        	throw new EntityNotFoundException();
        } else {
	    	if (authenticateAction(sialToken, DomainConstants.CONFIRM_EMAIL, id)) {
	    		user.setConfirmedEmail(true);
	    	}
	    	return user;
        }
	}
	
	@Override
	public boolean authenticateAction(String sialToken, String action, Long id) {
       	return SIAL.authenticate(sialToken, action, id, getEncodedPrivateKey());
	}
	
	@Override
	public String signAction(String action, Long id) {
		return SIAL.signAction(action, id, getEncodedPrivateKey(), ONE_WEEK);
	}
	
	private String getEncodedPrivateKey() {
		String privateKey = jwtProperties.getTokenPrivateKey();
		return Base64.getEncoder().encodeToString(privateKey.getBytes(UTF_8));
	}

	private String localizeErrorMessage(String errorCode, Object args[]) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(errorCode, args, locale);
	}

}

package com.thalasoft.user.rest.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.thalasoft.user.data.exception.CannotDeleteEntityException;
import com.thalasoft.user.data.exception.EntityAlreadyExistsException;
import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.exception.NoEntitiesFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ExceptionsHandler extends AbstractExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(ExceptionsHandler.class);
    
	@ExceptionHandler(CannotEncodePasswordException.class)
	@ResponseBody
	public ResponseEntity<ErrorInfo> cannotEncodePasswordException(HttpServletRequest request, CannotEncodePasswordException e) {
		String url = request.getRequestURL().toString();
		String errorMessage = localizeErrorMessage(e.getLocalizedMessage());
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.UNAUTHORIZED, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorInfo);
	}

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseBody
	public ResponseEntity<ErrorInfo> badCredentialsException(HttpServletRequest request, BadCredentialsException e) {
		String url = request.getRequestURL().toString();
		String errorMessage = localizeErrorMessage(e.getLocalizedMessage());
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.UNAUTHORIZED, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorInfo);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseBody
	public ResponseEntity<ErrorInfo> entityNotFoundException(HttpServletRequest request, EntityNotFoundException e) {
		String url = request.getRequestURL().toString();
		String errorMessage = localizeErrorMessage(e.getLocalizedMessage());
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.NOT_FOUND, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorInfo);
	}

	@ExceptionHandler(EntityAlreadyExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorInfo> entityAlreadyExistException(HttpServletRequest request, EntityAlreadyExistsException e) {
		String url = request.getRequestURL().toString();
		String errorMessage = localizeErrorMessage(e.getLocalizedMessage());
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.CONFLICT, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorInfo);
	}

	@ExceptionHandler(NoEntitiesFoundException.class)
	@ResponseBody
	public ResponseEntity<ErrorInfo> noEntitiesFoundException(HttpServletRequest request, NoEntitiesFoundException e) {
		String url = request.getRequestURL().toString();
		String errorMessage = localizeErrorMessage(e.getLocalizedMessage());
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.NOT_FOUND, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorInfo);
	}
	
	@ExceptionHandler(CannotDeleteEntityException.class)
	@ResponseBody
	public ResponseEntity<ErrorInfo> cannotDeleteEntityException(HttpServletRequest request, CannotDeleteEntityException e) {
		String url = request.getRequestURL().toString();
		String errorMessage = localizeErrorMessage(e.getLocalizedMessage());
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.FORBIDDEN, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorInfo);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity<ErrorFormInfo> methodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
		String url = request.getRequestURL().toString();
		String errorMessage = localizeErrorMessage("error.failed.controller.validation");
		ErrorFormInfo errorFormInfo = new ErrorFormInfo(url, HttpStatus.BAD_REQUEST, errorMessage);
		BindingResult result = e.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		errorFormInfo.getFieldErrors().addAll(populateFieldErrors(fieldErrors));
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorFormInfo);
	}

	private List<ErrorFormField> populateFieldErrors(List<FieldError> fieldErrorList) {
		List<ErrorFormField> errorFormFields = new ArrayList<ErrorFormField>();
		StringBuilder errorMessage = new StringBuilder("");
		for (FieldError fieldError : fieldErrorList) {
			errorMessage.append(fieldError.getCode()).append(".");
			errorMessage.append(fieldError.getObjectName()).append(".");
			errorMessage.append(fieldError.getField());
			errorFormFields.add(new ErrorFormField(fieldError.getField(), localizeErrorMessage(errorMessage.toString())));
			errorMessage.delete(0, errorMessage.capacity());
		}
		return errorFormFields;
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> missingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.http.request.missing.parameter", new Object[] { e.getParameterName() });
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.BAD_REQUEST, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> httpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.http.request.body.cannot.be.parsed");
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.BAD_REQUEST, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> illegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
    	String url = request.getRequestURL().toString();
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
    	String url = request.getRequestURL().toString();
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseBody
    public ResponseEntity<ErrorInfo> noHandlerFoundException(HttpServletRequest request, NoHandlerFoundException e) {
    	String url = request.getRequestURL().toString();
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> dataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.dao.constraint");
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.CONFLICT, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> invalidDataAccessApiUsageException(HttpServletRequest request, InvalidDataAccessApiUsageException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.dao");
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
    }
   
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    protected ResponseEntity<ErrorInfo> typeMismatchException(HttpServletRequest request, TypeMismatchException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.entity.id.invalid", new Object[] { e.getValue() });
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.BAD_REQUEST, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }
 
    @ExceptionHandler(NumberFormatException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> numberFormatException(HttpServletRequest request, NumberFormatException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.nfe");
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.BAD_REQUEST, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }
   
    @ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public ResponseEntity<ErrorInfo> nullPointerException(HttpServletRequest request, NullPointerException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.npe");
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
	}
	
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> runtimeException(HttpServletRequest request, RuntimeException e) {
    	String url = request.getRequestURL().toString();
    	String errorMessage = localizeErrorMessage("error.rte");
		ErrorInfo errorInfo = new ErrorInfo(url, HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
		logger.error(errorMessage);
		logger.debug(getStackTrace(e));
    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
    }
    
}

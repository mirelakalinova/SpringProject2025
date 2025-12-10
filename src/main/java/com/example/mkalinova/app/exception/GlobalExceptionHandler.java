package com.example.mkalinova.app.exception;

import com.example.mkalinova.app.land.Controller.BaseController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.thymeleaf.exceptions.TemplateProcessingException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {
	
	public static final String ERROR_VIEW = "error/error";
	public static final String ERROR_403 = "../../images/errors/403.jpg";
	public static final String ERROR = "../../images/errors/error.jpg";
	public static final String NO_SUCH_RESOURCE = "../../images/errors/no-such-resource.jpg";
	
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
		log.warn("Access denied for request {} {}. User: {}. Message: {}",
				request.getMethod(), request.getRequestURI(), request.getRemoteUser(), ex.getMessage());
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("heading", "Достъпът е отказан!");
		modelAndView.addObject("message", ex.getMessage());
		modelAndView.addObject("image", ERROR_403);
		return modelAndView;
	}
	
	@ExceptionHandler(NullPointerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
		log.error("NullPointerException at request {} {}. User: {}", request.getMethod(), request.getRequestURI(), request.getRemoteUser(), ex);
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("heading", "Нещо се обърка!");
		modelAndView.addObject("message", ex.getMessage());
		modelAndView.addObject("image", ERROR);
		return modelAndView;
	}
	
	@ExceptionHandler(NoSuchResourceException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView handleNoSuchResourceException(NoSuchResourceException ex, HttpServletRequest request) {
		log.info("No such resource: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("heading", "Няма намерен ресурс!");
		modelAndView.addObject("message", ex.getMessage());
		modelAndView.addObject("image", NO_SUCH_RESOURCE);
		return modelAndView;
	}
	
	@ExceptionHandler(TemplateProcessingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleTemplateError(TemplateProcessingException ex, HttpServletRequest request) {
		log.error("Template error at {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("heading", "Грешка при рендер на шаблон");
		modelAndView.addObject("message", ex.getMessage());
		modelAndView.addObject("image", ERROR);
		return modelAndView;
	}
	
	@ExceptionHandler(SpelEvaluationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleSpelError(SpelEvaluationException ex, HttpServletRequest request) {
		log.error("SpEL evaluation error at {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("heading", "Грешка при обработка на шаблон");
		modelAndView.addObject("message", ex.getMessage());
		modelAndView.addObject("image", ERROR);
		return modelAndView;
	}
	
	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
		log.warn("No handler found for request {} {}", request.getMethod(), request.getRequestURI());
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("heading", "404");
		modelAndView.addObject("message", "СТРАНИЦАТА НЕ СЪЩЕСТВУВА!");
		modelAndView.addObject("image", NO_SUCH_RESOURCE);
		return modelAndView;
	}
	
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
			throw e;
		}
		
		log.error("Unhandled exception for request {} {}: {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("message", "Възникна грешка на сървъра.");
		modelAndView.addObject("heading", "ГРЕШКА 500");
		modelAndView.addObject("image", ERROR);
		return modelAndView;
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
		log.warn("Type mismatch for {} {} : param={} requiredType={} message={}",
				request.getMethod(), request.getRequestURI(),
				ex.getName(), ex.getRequiredType(), ex.getMessage());
		ModelAndView modelAndView = super.view(ERROR_VIEW);
		modelAndView.addObject("heading", "Невалиден параметър");
		modelAndView.addObject("message", "Идентификаторът е във неправилен формат.");
		modelAndView.addObject("image", ERROR);
		
		return modelAndView;
	}
}

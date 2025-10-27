package com.example.mkalinova.app.exepction;

import com.example.mkalinova.app.Land.Controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    public static final String ERROR_VIEW = "errors/error";
    public static final String ERROR_403 = "../../images/errors/403.jpg";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        ModelAndView modelAndView = super.view(ERROR_VIEW);
        modelAndView.addObject("heading","Достъпът е отказан!");
        modelAndView.addObject("message", ex.getMessage());
//        modelAndView.addObject("image", ERROR_403);
        return modelAndView;
    }
}

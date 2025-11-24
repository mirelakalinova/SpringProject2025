package com.example.mkalinova.app.exepction;

import com.example.mkalinova.app.land.Controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    public static final String ERROR_VIEW = "errors/error";
    public static final String ERROR_403 = "../../images/errors/403.jpg";
    public static final String ERROR = "../../images/errors/error.jpg";
    public static final String NO_SUCH_RESOURCE = "../../images/errors/no-such-resource.jpg";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        ModelAndView modelAndView = super.view(ERROR_VIEW);
        modelAndView.addObject("heading","Достъпът е отказан!");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("image", ERROR_403);
        return modelAndView;
    }


    @ExceptionHandler
    @ResponseStatus
    public ModelAndView handleNullPointerException(NullPointerException ex) {
        ModelAndView modelAndView = super.view(ERROR_VIEW);
        modelAndView.addObject("heading","Нещо се обърка!");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("image", ERROR);
        return modelAndView;
    }


//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ModelAndView ResourceNotFoundException(ResourceNotFoundException ex) {
//        ModelAndView modelAndView = super.view(ERROR_VIEW);
//            modelAndView.addObject("heading","Няма намерен такъв ресуср!");
//        modelAndView.addObject("message", ex.getMessage());
//        modelAndView.addObject("image", NO_SUCH_RESOURCE);
//        return modelAndView;
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView ResponseStatusException(ResponseStatusException ex) {
        ModelAndView modelAndView = super.view(ERROR_VIEW);
        modelAndView.addObject("heading","Няма намерен такъв ресуср!");
        modelAndView.addObject("message", ex.getReason());
        modelAndView.addObject("image", NO_SUCH_RESOURCE);
        return modelAndView;
    }
}

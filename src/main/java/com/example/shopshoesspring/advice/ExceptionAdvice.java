package com.example.shopshoesspring.advice;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(Model model) {
        return "error/invaliduserdata";
    }
}

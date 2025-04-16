package com.codewithmada.spring6mvc.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //picked up globally
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException() {
        System.out.println("In exception handler method");
        return ResponseEntity.notFound().build();
    }
}

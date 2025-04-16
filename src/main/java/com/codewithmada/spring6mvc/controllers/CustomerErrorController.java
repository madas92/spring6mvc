package com.codewithmada.spring6mvc.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class CustomerErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleErrors(MethodArgumentNotValidException e){

        List errorList = e.getFieldErrors().stream()
                .map( fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).collect(toList());

        return ResponseEntity.badRequest().body((e.getBindingResult().getFieldErrors()));
    }
    @ExceptionHandler
    ResponseEntity handleJPAViolations(TransactionSystemException e){
        ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest();

        if (e.getCause().getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException ve = (ConstraintViolationException) e.getCause().getCause();
            List errors = ve.getConstraintViolations().stream().map(
                    constraintViolation -> {
                        Map<String, String> errMap = new HashMap<>();
                        errMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                    return errMap;
                    }
            ).collect(toList());
            return responseEntity.body(errors);
        }

        return responseEntity.build();
    }
}

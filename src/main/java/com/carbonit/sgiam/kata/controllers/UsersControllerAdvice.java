package com.carbonit.sgiam.kata.controllers;

import com.carbonit.sgiam.kata.dtos.ErrorResponseDTO;
import com.carbonit.sgiam.kata.exceptions.ParameterNotValidException;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = UsersController.class)
@RequestMapping(produces = "application/json")
public class UsersControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> notFoundException(final UserNotFoundException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setCode(HttpStatus.NOT_FOUND.toString());
        errorResponseDTO.setMessage(e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ParameterNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> assertionException(final ParameterNotValidException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setCode(HttpStatus.BAD_REQUEST.toString());
        errorResponseDTO.setMessage(e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

}

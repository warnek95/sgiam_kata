package com.carbonit.sgiam.kata.controllers;

import com.carbonit.sgiam.kata.dtos.ErrorResponseDTO;
import com.carbonit.sgiam.kata.exceptions.ParameterNotValidException;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = UsersController.class)
@RequestMapping(produces = "application/json")
public class UsersControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ApiResponses(value = { @ApiResponse(responseCode = "404", description = "No user was found", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}
    )})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> notFoundException(final UserNotFoundException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setCode(HttpStatus.NOT_FOUND.toString());
        errorResponseDTO.setMessage(e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ParameterNotValidException.class)
    @ApiResponses(value = { @ApiResponse(responseCode = "400", description = "The provided parameters are invalid", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))}
    )})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> assertionException(final ParameterNotValidException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setCode(HttpStatus.BAD_REQUEST.toString());
        errorResponseDTO.setMessage(e.getLocalizedMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

}

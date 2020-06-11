package com.carbonit.sgiam.kata.controllers;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.exceptions.ParameterNotValidException;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import com.carbonit.sgiam.kata.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.carbonit.sgiam.kata.exceptions.ParameterNotValidException.ErrorMessage.INVALID_UUID;
import static com.carbonit.sgiam.kata.exceptions.ParameterNotValidException.ErrorMessage.NAME_CONTAINS_ADMIN;
import static org.springframework.data.domain.PageRequest.of;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
public class UsersController {

    public static final String ADMIN = "admin";
    private final UsersService service;

    UsersController(UsersService service){
        this.service = service;
    }


    @Operation(summary = "Create a user")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "User created", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}
    )})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO postUser(@RequestBody final UserDTO user) throws ParameterNotValidException {
        checkUserValidity(user);
        return service.createUser(user);
    }

    @Operation(summary = "Get a user by Id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Found user", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}
    )})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable("id") final String idAsString) throws UserNotFoundException, ParameterNotValidException {
        try {
            UUID id = UUID.fromString(idAsString);
            return service.findUserById(id);
        } catch (IllegalArgumentException e){
            throw new ParameterNotValidException(INVALID_UUID, idAsString);
        }
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found users", content = {
            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))}),
        @ApiResponse(responseCode = "204", description = "No users found", content = @Content) })
    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = service.findAllUsers();
        if(users.isEmpty()) return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get all users by filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "found users", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))}),
            @ApiResponse(responseCode = "204", description = "No Users found", content = @Content) })
    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDTO>> filterUsers(@RequestParam("page") final Integer page, @RequestParam("size") final Integer size) {
        Page<UserDTO> users = service.filterUsers(of(page, size));
        if(users.isEmpty()) return new ResponseEntity<>(users.getContent(), HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(users.getContent(), HttpStatus.OK);
    }

    @Operation(summary = "Update a user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User updated", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}
    )})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable("id") final String idAsString, @RequestBody final UserDTO user) throws UserNotFoundException, ParameterNotValidException {
        checkUserValidity(user);
        try {
            UUID id = UUID.fromString(idAsString);
            return service.updateUser(id, user);
        } catch (IllegalArgumentException e){
            throw new ParameterNotValidException(INVALID_UUID, idAsString);
        }
    }

    private void checkUserValidity(UserDTO user) throws ParameterNotValidException {
        if (user.getName().contains(ADMIN)) throw new ParameterNotValidException(NAME_CONTAINS_ADMIN, user.getName());
    }

    @Operation(summary = "Delete a user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User deleted", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}
    )})
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO deleteUser(@PathVariable("id") final String idAsString) throws UserNotFoundException, ParameterNotValidException {
        try {
            UUID id = UUID.fromString(idAsString);
            return service.deleteUser(id);
        } catch (IllegalArgumentException e){
            throw new ParameterNotValidException(INVALID_UUID, idAsString);
        }
    }

}

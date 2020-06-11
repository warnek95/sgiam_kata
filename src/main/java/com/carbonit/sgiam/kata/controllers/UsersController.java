package com.carbonit.sgiam.kata.controllers;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.exceptions.ParameterNotValidException;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import com.carbonit.sgiam.kata.services.UsersService;
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

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO postUser(@RequestBody final UserDTO user) throws ParameterNotValidException {
        checkUserValidity(user);
        return service.createUser(user);
    }

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

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = service.findAllUsers();
        if(users.isEmpty()) return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDTO>> filterUsers(@RequestParam("page") final Integer page, @RequestParam("size") final Integer size) {
        Page<UserDTO> users = service.filterUsers(of(page, size));
        if(users.isEmpty()) return new ResponseEntity<>(users.getContent(), HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(users.getContent(), HttpStatus.OK);
    }

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

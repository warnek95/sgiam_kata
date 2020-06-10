package com.carbonit.sgiam.kata.controllers;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
public class UsersController {

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO postUser() {
        return new UserDTO();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable("id") final String id) {
        return new UserDTO();
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAllUsers() {
        return new ArrayList<>();
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDTO> filterUsers(Pageable pageable) {
        return Page.empty();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable("id") final String id, @RequestBody final UserDTO user) {
        return new UserDTO();
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@PathVariable("id") final String id) {
        return "";
    }

}

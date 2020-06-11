package com.carbonit.sgiam.kata.services;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsersService {

    public UserDTO createUser(final UserDTO userDTO) {
        return null;
    }

    public UserDTO findUserById(final UUID id) {
        return null;
    }

    public List<UserDTO> findAllUsers() {
        return new ArrayList<>();
    }

    public Page<UserDTO> filterUsers(Pageable pageable) {
        return new PageImpl(new ArrayList());
    }

    public UserDTO updateUser(final UUID id, final UserDTO userDTO) {
        return null;
    }

    public UUID deleteUser(final UUID id) {
        return null;
    }
}

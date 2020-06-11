package com.carbonit.sgiam.kata.services;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UsersService {
    public UserDTO createUser(final UserDTO userDTO);

    public UserDTO findUserById(final UUID id) throws UserNotFoundException;

    public List<UserDTO> findAllUsers();

    public Page<UserDTO> filterUsers(Pageable pageable);

    public UserDTO updateUser(final UUID id, final UserDTO userDTO) throws UserNotFoundException;

    public UserDTO deleteUser(final UUID id) throws UserNotFoundException;
}

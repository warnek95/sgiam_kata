package com.carbonit.sgiam.kata.services;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import com.carbonit.sgiam.kata.mappers.UserMapper;
import com.carbonit.sgiam.kata.mappers.UserMapperImpl;
import com.carbonit.sgiam.kata.models.User;
import com.carbonit.sgiam.kata.repositories.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository repository;
    private final UserMapper userMapper = new UserMapperImpl();

    UsersServiceImpl(UsersRepository repository){
        this.repository = repository;
    }

    public UserDTO createUser(final UserDTO userDTO) {
        return userMapper.toDto(repository.save(userMapper.toEntity(userDTO)));
    }

    public UserDTO findUserById(final UUID id) throws UserNotFoundException {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
        return userMapper.toDto(user);
    }

    public List<UserDTO> findAllUsers() {
        return stream(repository.findAll().spliterator(), false)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<UserDTO> filterUsers(Pageable pageable) {
        return repository.findAll(pageable).map(userMapper::toDto);
    }

    public UserDTO updateUser(final UUID id, final UserDTO userDTO) throws UserNotFoundException {
        return repository.findById(id)
            .map(user -> {
                user.setName(userDTO.getName());
                return userMapper.toDto(repository.save(user));
            })
            .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public UserDTO deleteUser(final UUID id) throws UserNotFoundException {
        return repository.findById(id)
            .map(user -> {
                repository.delete(user);
                return userMapper.fromId(id);
            })
            .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }
}

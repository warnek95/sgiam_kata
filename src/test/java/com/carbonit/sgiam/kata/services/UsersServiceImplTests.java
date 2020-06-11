package com.carbonit.sgiam.kata.services;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import com.carbonit.sgiam.kata.models.User;
import com.carbonit.sgiam.kata.repositories.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static com.carbonit.sgiam.kata.utils.UserUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.PageRequest.of;

@ExtendWith(MockitoExtension.class)
public class UsersServiceImplTests {

    @Mock
    private UsersRepository repository;

    @InjectMocks
    private UsersServiceImpl service;

    private final UserDTO userDTO1 = createUserDTO(USER_ID_1, USER_NAME_1);
    private final UserDTO userDTO2 = createUserDTO(USER_ID_2, USER_NAME_2);

    private final User user1 = createUserEntity(USER_ID_1, USER_NAME_1);
    private final User user2 = createUserEntity(USER_ID_2, USER_NAME_2);

    /*********************** findAll ***********************/

    @DisplayName("As a user I can get all users")
    @Test
    void findAllUsers() {
        BDDMockito.given(repository.findAll()).willReturn(new ArrayList<>(Arrays.asList(user1, user2)));

        List<UserDTO> users = service.findAllUsers();

        assertEquals(2, users.size());
        assertEquals(userDTO1, users.get(0));
        assertEquals(userDTO2, users.get(1));
    }

    @DisplayName("As a user I get an empty list when there are no users to get")
    @Test
    void findAllUsersButNoUsers() {
        BDDMockito.given(repository.findAll()).willReturn(new ArrayList<>());

        List<UserDTO> users = service.findAllUsers();

        assertEquals(0, users.size());
    }

    /*********************** getUserById ***********************/

    @DisplayName("As a user I can get a specific User by id")
    @Test
    void getUserById() throws UserNotFoundException {
        BDDMockito.given(repository.findById(UUID.fromString(USER_ID_1))).willReturn(Optional.of(user1));

        UserDTO user = service.findUserById(UUID.fromString(USER_ID_1));

        assertEquals(USER_ID_1, user.getId().toString());
        assertEquals(USER_NAME_1, user.getName());
    }

    @DisplayName("As a user I cannot get a user when I give a non existing id")
    @Test
    void getUserByIdWithANonExistingId() {
        BDDMockito.given(repository.findById(UUID.fromString(NON_EXISTING_ID))).willReturn(Optional.empty());

        Executable executable = () -> {
            UserDTO user = service.findUserById(UUID.fromString(NON_EXISTING_ID));
        };

        assertThrows(UserNotFoundException.class, executable);
    }

    /*********************** filterUsers ***********************/

    @DisplayName("As a user I can filter the users I want to get")
    @Test
    void filterUsers() {
        BDDMockito.given(repository.findAll(of(0, 1))).willReturn(new PageImpl(new ArrayList<>(Collections.singletonList(user1))));

        Page<UserDTO> users = service.filterUsers(of(0, 1));

        assertEquals(1, users.getTotalElements());
        assertEquals(USER_ID_1, users.getContent().get(0).getId().toString());
        assertEquals(USER_NAME_1, users.getContent().get(0).getName());
    }

    @DisplayName("As a user I get no users when no user satisfy the filter")
    @Test
    void filterUsersButNoUsers() {
        BDDMockito.given(repository.findAll(of(2, 10))).willReturn(new PageImpl(new ArrayList<>()));

        Page<UserDTO> users =  service.filterUsers(of(2, 10));

        assertEquals(0, users.getTotalElements());
    }

    /*********************** postUser ***********************/

    @DisplayName("As a user I can create a user")
    @Test
    void createUser() {
        UserDTO userDTOToCreate = createUserDTO(null, USER_NAME_1);
        User userEntityToCreate = createUserEntity(null, USER_NAME_1);

        BDDMockito.given(repository.save(userEntityToCreate)).willReturn(user1);

        UserDTO user = service.createUser(userDTOToCreate);

        assertEquals(USER_ID_1, user.getId().toString());
        assertEquals(USER_NAME_1, user.getName());
    }

    /*********************** putUser ***********************/

    @DisplayName("As a user I can update a user")
    @Test
    void updateUser() throws UserNotFoundException {
        UserDTO userDTOToUpdate = createUserDTO(USER_ID_1, UPDATE_USER_NAME);
        User userEntityToUpdate = createUserEntity(USER_ID_1, UPDATE_USER_NAME);

        BDDMockito.given(repository.findById(UUID.fromString(USER_ID_1))).willReturn(Optional.of(user1));
        BDDMockito.given(repository.save(userEntityToUpdate)).willReturn(userEntityToUpdate);

        UserDTO user = service.updateUser(UUID.fromString(USER_ID_1), userDTOToUpdate);

        assertEquals(USER_ID_1, user.getId().toString());
        assertEquals(UPDATE_USER_NAME, user.getName());
    }

    @DisplayName("As a user I cannot update a user with a non existing id")
    @Test
    void updateUserByIdWithANonExistingId() throws UserNotFoundException {
        UserDTO userDTOToUpdate = createUserDTO(NON_EXISTING_ID, UPDATE_USER_NAME);

        BDDMockito.given(repository.findById(UUID.fromString(NON_EXISTING_ID))).willReturn(Optional.empty());

        Executable executable = () -> {
            UserDTO user = service.updateUser(UUID.fromString(NON_EXISTING_ID), userDTOToUpdate);
        };

        assertThrows(UserNotFoundException.class, executable);
    }

    /*********************** deleteUser ***********************/

    @DisplayName("As a user I can delete a user")
    @Test
    void deleteUser() throws UserNotFoundException {
        BDDMockito.given(repository.findById(UUID.fromString(USER_ID_1))).willReturn(Optional.of(user1));

        UserDTO user = service.deleteUser(UUID.fromString(USER_ID_1));

        BDDMockito.verify(repository).findById(UUID.fromString(USER_ID_1));
        BDDMockito.verify(repository).delete(user1);
        BDDMockito.verifyNoMoreInteractions(repository);

        assertEquals(USER_ID_1, user.getId().toString());
        assertNull(user.getName());
    }

    @DisplayName("As a user I cannot delete a user with a non existing id")
    @Test
    void deleteUserByIdWithANonExistingId() throws UserNotFoundException {
        BDDMockito.given(repository.findById(UUID.fromString(NON_EXISTING_ID))).willReturn(Optional.empty());

        Executable executable = () -> {
            UserDTO user = service.deleteUser(UUID.fromString(NON_EXISTING_ID));
        };

        assertThrows(UserNotFoundException.class, executable);
    }
}
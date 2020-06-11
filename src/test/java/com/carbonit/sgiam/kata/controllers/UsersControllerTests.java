package com.carbonit.sgiam.kata.controllers;

import com.carbonit.sgiam.kata.dtos.ErrorResponseDTO;
import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.exceptions.ParameterNotValidException;
import com.carbonit.sgiam.kata.exceptions.UserNotFoundException;
import com.carbonit.sgiam.kata.services.UsersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.carbonit.sgiam.kata.exceptions.UserNotFoundException.USER_NOT_FOUND_MSG;
import static com.carbonit.sgiam.kata.utils.UserUtils.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.PageRequest.of;

@ExtendWith(SpringExtension.class)
public class UsersControllerTests {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @MockBean
    private UsersService usersService;

    private final UserDTO user1 = createUserDTO(USER_ID_1, USER_NAME_1);
    private final UserDTO user2 = createUserDTO(USER_ID_2, USER_NAME_2);

    /*********************** getAll ***********************/
    
    @DisplayName("As a user I can get all users")
    @Test
    void getAllUsers() {
        BDDMockito.given(usersService.findAllUsers()).willReturn(new ArrayList<>(Arrays.asList(user1, user2)));

        UserDTO[] users =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get("/api/v1/users")
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserDTO[].class);

        assertEquals(2, users.length);
        assertTrue(Arrays.stream(users).anyMatch(user ->
            user.getName().equals(USER_NAME_1) && user.getId().toString().equals(USER_ID_1)
        ));
        assertTrue(Arrays.stream(users).anyMatch(user ->
            user.getName().equals(USER_NAME_2) && user.getId().toString().equals(USER_ID_2)
        ));
    }

    @DisplayName("As a user I get a no content when there are no users to get")
    @Test
    void getAllUsersButNoUsers() {
        BDDMockito.given(usersService.findAllUsers()).willReturn(new ArrayList<>());

        UserDTO[] users =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get("/api/v1/users")
                .then().statusCode(HttpStatus.NO_CONTENT.value())
                .extract()
                .as(UserDTO[].class);

        assertEquals(0, users.length);
    }

    /*********************** getUserById ***********************/

    @DisplayName("As a user I can get a specific User by id")
    @Test
    void getUserById() throws UserNotFoundException {
        BDDMockito.given(usersService.findUserById(UUID.fromString(USER_ID_1))).willReturn(user1);

        UserDTO user =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get(String.format("/api/v1/users/%s", USER_ID_1))
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserDTO.class);

        assertEquals(USER_ID_1, user.getId().toString());
        assertEquals(USER_NAME_1, user.getName());
    }

    @DisplayName("As a user I cannot get a user with an invalid id")
    @Test
    void getUserByIdWithInvalidId() {
        ErrorResponseDTO error =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get(String.format("/api/v1/users/%s", INVALID_ID))
                .then().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponseDTO.class);

        assertEquals(error.getMessage(),
            String.format(ParameterNotValidException.ErrorMessage.INVALID_UUID.getMessageTemplate(), INVALID_ID));
    }

    @DisplayName("As a user I cannot get a user when I give a non existing id")
    @Test
    void getUserByIdWithANonExistingId() throws UserNotFoundException {
        BDDMockito.given(usersService.findUserById(UUID.fromString(NON_EXISTING_ID))).willThrow(new UserNotFoundException(NON_EXISTING_ID));

        ErrorResponseDTO error =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get(String.format("/api/v1/users/%s", NON_EXISTING_ID))
                .then().statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ErrorResponseDTO.class);

        assertEquals(error.getMessage(), String.format(USER_NOT_FOUND_MSG, NON_EXISTING_ID));
    }

    /*********************** filterUsers ***********************/

    @DisplayName("As a user I can filter the users I want to get")
    @Test
    void filterUsers() {
        BDDMockito.given(usersService.filterUsers(of(0, 1))).willReturn(new PageImpl(new ArrayList<>(Collections.singletonList(user1))));

        UserDTO[] users =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get("/api/v1/users/filter?page=0&size=1")
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserDTO[].class);

        assertEquals(1, users.length);
        assertEquals(USER_ID_1, users[0].getId().toString());
        assertEquals(USER_NAME_1, users[0].getName());
    }

    @DisplayName("As a user I get no users when no user satisfy the filter")
    @Test
    void filterUsersButNoUsers() {
        BDDMockito.given(usersService.filterUsers(of(2, 10))).willReturn(new PageImpl(new ArrayList<>()));

        UserDTO[] users =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get("/api/v1/users/filter?page=2&size=10")
                .then().statusCode(HttpStatus.NO_CONTENT.value())
                .extract()
                .as(UserDTO[].class);

        assertEquals(0, users.length);
    }

    /*********************** postUser ***********************/

    @DisplayName("As a user I can create a user")
    @Test
    void postUser() throws JsonProcessingException {
        UserDTO mockedUser = createUserDTO(null, USER_NAME_1);
        BDDMockito.given(usersService.createUser(mockedUser)).willReturn(user1);

        Map<String, String> request = new HashMap<>();
        request.put("name", USER_NAME_1);

        UserDTO user =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(this.objectMapper.writeValueAsString(request))
            .when()
                .post("/api/v1/users")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(UserDTO.class);

        assertEquals(USER_ID_1, user.getId().toString());
        assertEquals(USER_NAME_1, user.getName());
    }

    @DisplayName("As a user I cannot create a user with an invalid name")
    @Test
    void postInvalidUserData() throws JsonProcessingException {
        Map<String, String> request = new HashMap<>();
        request.put("name", INVALID_NAME);

        ErrorResponseDTO error =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(this.objectMapper.writeValueAsString(request))
            .when()
                .post("/api/v1/users")
                .then().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponseDTO.class);

        assertEquals(error.getMessage(),
            String.format(ParameterNotValidException.ErrorMessage.NAME_CONTAINS_ADMIN.getMessageTemplate(), INVALID_NAME));
    }

    /*********************** putUser ***********************/

    @DisplayName("As a user I can update a user")
    @Test
    void putUser() throws JsonProcessingException, UserNotFoundException {
        UserDTO mockedUser = createUserDTO(USER_ID_1, UPDATE_USER_NAME);
        BDDMockito.given(usersService.updateUser(UUID.fromString(USER_ID_1), mockedUser)).willReturn(mockedUser);

        Map<String, String> request = new HashMap<>();
        request.put("id", USER_ID_1);
        request.put("name", UPDATE_USER_NAME);

        UserDTO user =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(this.objectMapper.writeValueAsString(request))
            .when()
                .put(String.format("/api/v1/users/%s", USER_ID_1))
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserDTO.class);

        assertEquals(USER_ID_1, user.getId().toString());
        assertEquals(UPDATE_USER_NAME, user.getName());
    }

    @DisplayName("As a user I cannot update a user with an invalid name")
    @Test
    void putInvalidUserData() throws JsonProcessingException {
        Map<String, String> request = new HashMap<>();
        request.put("id", USER_ID_1);
        request.put("name", INVALID_NAME);

        ErrorResponseDTO error =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(this.objectMapper.writeValueAsString(request))
            .when()
                .put(String.format("/api/v1/users/%s", USER_ID_1))
                .then().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponseDTO.class);

        assertEquals(error.getMessage(),
            String.format(ParameterNotValidException.ErrorMessage.NAME_CONTAINS_ADMIN.getMessageTemplate(), INVALID_NAME));
    }

    @DisplayName("As a user I cannot update a user with a non existing id")
    @Test
    void putUserByIdWithANonExistingId() throws JsonProcessingException, UserNotFoundException {
        UserDTO mockedUser = createUserDTO(NON_EXISTING_ID, UPDATE_USER_NAME);
        BDDMockito.given(usersService.updateUser(UUID.fromString(NON_EXISTING_ID), mockedUser)).willThrow(new UserNotFoundException(NON_EXISTING_ID));

        Map<String, String> request = new HashMap<>();
        request.put("id", NON_EXISTING_ID);
        request.put("name", UPDATE_USER_NAME);

        ErrorResponseDTO error =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(this.objectMapper.writeValueAsString(request))
            .when()
                .put(String.format("/api/v1/users/%s", NON_EXISTING_ID))
                .then().statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ErrorResponseDTO.class);

        assertEquals(error.getMessage(), String.format(USER_NOT_FOUND_MSG, NON_EXISTING_ID));
    }

    /*********************** deleteUser ***********************/

    @DisplayName("As a user I can delete a user")
    @Test
    void deleteUser() throws UserNotFoundException {
        UserDTO mockedUser = createUserDTO(USER_ID_1, null);
        
        BDDMockito.given(usersService.deleteUser(UUID.fromString(USER_ID_1))).willReturn(mockedUser);

        UserDTO user =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .delete(String.format("/api/v1/users/%s", USER_ID_1))
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserDTO.class);

        assertEquals(USER_ID_1, user.getId().toString());
        assertNull(user.getName());
    }

    @DisplayName("As a user I cannot delete a user with an invalid id")
    @Test
    void deleteUserWithInvalidId() {
        ErrorResponseDTO error =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .delete(String.format("/api/v1/users/%s", INVALID_ID))
                .then().statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ErrorResponseDTO.class);

        assertEquals(error.getMessage(),
            String.format(ParameterNotValidException.ErrorMessage.INVALID_UUID.getMessageTemplate(), INVALID_ID));
    }

    @DisplayName("As a user I cannot delete a user with a non existing id")
    @Test
    void deleteUserByIdWithANonExistingId() throws UserNotFoundException {
        BDDMockito.given(usersService.deleteUser(UUID.fromString(NON_EXISTING_ID))).willThrow(new UserNotFoundException(NON_EXISTING_ID));

        ErrorResponseDTO error =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .delete(String.format("/api/v1/users/%s", NON_EXISTING_ID))
                .then().statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ErrorResponseDTO.class);

        assertEquals(error.getMessage(), String.format(USER_NOT_FOUND_MSG, NON_EXISTING_ID));
    }
}

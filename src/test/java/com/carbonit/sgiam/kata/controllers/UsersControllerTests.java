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
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;

import static com.carbonit.sgiam.kata.exceptions.UserNotFoundException.USER_NOT_FOUND_MSG;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.PageRequest.of;

@WebMvcTest(UsersController.class)
public class UsersControllerTests {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @MockBean
    private UsersService usersService;
    
    private static final String USER_NAME_1 = "user_name";
    private static final String USER_NAME_2 = "friendly_user";
    private static final String UPDATE_USER_NAME = "new_name";
    private static final String INVALID_NAME = "admin";
    private static final String USER_ID_1 = "eed93d8f-e053-4d75-af63-060f1f8f92d7";
    private static final String USER_ID_2 = "865fdcc4-cd19-4ab9-9f2b-5309a32c8e9e";
    private static final String INVALID_ID = "invalid_id";
    private static final String NON_EXISTING_ID = "c182c9ba-304c-4290-8be5-1cf46045b474";
    
    private final UserDTO user1 = createUser(USER_ID_1, USER_NAME_1);
    private final UserDTO user2 = createUser(USER_ID_2, USER_NAME_2);

    private UserDTO createUser(String id, String name) {
        UserDTO user = new UserDTO();
        user.setId(UUID.fromString(id));
        user.setName(name);
        return user;
    }

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

        assertEquals(users.length, 2);
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

        assertEquals(users.length, 0);
    }

    /*********************** getUserById ***********************/

    @DisplayName("As a user I can get a specific User by id")
    @Test
    void getUserById() {
        BDDMockito.given(usersService.findUserById(UUID.fromString(USER_ID_1))).willReturn(user1);

        UserDTO user =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .get(String.format("/api/v1/users/%s", USER_ID_1))
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UserDTO.class);

        assertEquals(user.getId().toString(), USER_ID_1);
        assertEquals(user.getName(), USER_NAME_1);
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
    void getUserByIdWithANonExistingId() {
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

        assertEquals(users.length, 1);
        assertEquals(users[0].getId().toString(), USER_ID_1);
        assertEquals(users[0].getName(), USER_NAME_1);
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
        UserDTO u = new UserDTO();
        u.setName(USER_NAME_1);
        BDDMockito.given(usersService.createUser(u)).willReturn(user1);

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

        assertEquals(user.getId().toString(), USER_ID_1);
        assertEquals(user.getName(), USER_NAME_1);
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
    void putUser() throws JsonProcessingException {
        UserDTO u = new UserDTO();
        u.setId(UUID.fromString(USER_ID_1));
        u.setName(UPDATE_USER_NAME);
        BDDMockito.given(usersService.updateUser(UUID.fromString(USER_ID_1), u)).willReturn(u);

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

        assertEquals(user.getId().toString(), USER_ID_1);
        assertEquals(user.getName(), UPDATE_USER_NAME);
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
    void putUserByIdWithANonExistingId() throws JsonProcessingException {
        UserDTO u = new UserDTO();
        u.setId(UUID.fromString(NON_EXISTING_ID));
        u.setName(UPDATE_USER_NAME);
        BDDMockito.given(usersService.updateUser(UUID.fromString(NON_EXISTING_ID), u)).willThrow(new UserNotFoundException(NON_EXISTING_ID));

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
    void deleteUser() {
        BDDMockito.given(usersService.deleteUser(UUID.fromString(USER_ID_1))).willReturn(UUID.fromString(USER_ID_1));

        UUID uuid =
            given()
                .standaloneSetup(new UsersController(usersService), new UsersControllerAdvice())
            .when()
                .delete(String.format("/api/v1/users/%s", USER_ID_1))
                .then().statusCode(HttpStatus.OK.value())
                .extract()
                .as(UUID.class);

        assertEquals(uuid.toString(), USER_ID_1);
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
    void deleteUserByIdWithANonExistingId() {
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

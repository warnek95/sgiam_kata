package com.carbonit.sgiam.kata.utils;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.models.User;

import java.util.UUID;

public class UserUtils {

    public static final String USER_NAME_1 = "user_name";
    public static final String USER_NAME_2 = "friendly_user";
    public static final String UPDATE_USER_NAME = "new_name";
    public static final String INVALID_NAME = "admin";
    public static final String USER_ID_1 = "eed93d8f-e053-4d75-af63-060f1f8f92d7";
    public static final String USER_ID_2 = "865fdcc4-cd19-4ab9-9f2b-5309a32c8e9e";
    public static final String INVALID_ID = "invalid_id";
    public static final String NON_EXISTING_ID = "c182c9ba-304c-4290-8be5-1cf46045b474";

    public static UserDTO createUserDTO(String id, String name) {
        UserDTO user = new UserDTO();
        if(id != null) user.setId(UUID.fromString(id));
        if(name != null) user.setName(name);
        return user;
    }

    public static User createUserEntity(String id, String name) {
        User user = new User();
        if(id != null) user.setId(UUID.fromString(id));
        if(name != null) user.setName(name);
        return user;
    }
}

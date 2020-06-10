package com.carbonit.sgiam.kata.exceptions;

public class UserNotFoundException extends RuntimeException {

    public static final String USER_NOT_FOUND_MSG = "No user was found with the id: %s";

    public UserNotFoundException(final String id) {
        super(String.format(USER_NOT_FOUND_MSG, id));
    }
}

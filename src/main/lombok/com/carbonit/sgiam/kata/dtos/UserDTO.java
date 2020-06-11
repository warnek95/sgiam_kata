package com.carbonit.sgiam.kata.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserDTO implements Serializable {
    private UUID id;
    private String name;
}

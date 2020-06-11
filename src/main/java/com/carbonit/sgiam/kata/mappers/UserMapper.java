package com.carbonit.sgiam.kata.mappers;

import com.carbonit.sgiam.kata.dtos.UserDTO;
import com.carbonit.sgiam.kata.models.User;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserMapper {
    UserDTO toDto(final User user);

    List<UserDTO> toDto(final List<User> users);

    User toEntity(final UserDTO userDTO);

    List<User> toEntity(final List <UserDTO> userDTOs);

    default UserDTO fromId(final UUID id) {

        if (id == null) {
            return null;
        }

        final UserDTO userDTO=new UserDTO();
        userDTO.setId(id);

        return userDTO;
    }
}

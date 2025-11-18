package com.sergioag.clinicare_api.mapper;

import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Only one User
    UserResponseDTO toUserResponseDTO(User user);

    // List of Users
    List<UserResponseDTO> toUserResponseDTOs(List<User> users);
}

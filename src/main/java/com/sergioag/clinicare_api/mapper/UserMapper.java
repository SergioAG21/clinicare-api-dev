package com.sergioag.clinicare_api.mapper;

import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.Role;
import com.sergioag.clinicare_api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Only one User
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToNames")
    UserResponseDTO toUserResponseDTO(User user);

    // List of Users
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToNames")
    List<UserResponseDTO> toUserResponseDTOs(List<User> users);

    @Named("mapRolesToNames")
    default Set<String> mapRolesToNames(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}

package com.sergioag.clinicare_api.mapper;

import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "profileImageUrl", source = "profileImageUrl")
    @Mapping(target = "roles", source = "userRoles", qualifiedByName = "mapUserRolesToNames")
    @Mapping(target = "specialty", source = "userRoles", qualifiedByName = "mapDoctorSpecialty")
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "profileImageUrl", source = "profileImageUrl")
    @Mapping(target = "roles", source = "userRoles", qualifiedByName = "mapUserRolesToNames")
    @Mapping(target = "specialty", source = "userRoles", qualifiedByName = "mapDoctorSpecialty")
    List<UserResponseDTO> toUserResponseDTOs(List<User> users);

    @Named("mapUserRolesToNames")
    default Set<String> mapUserRolesToNames(Collection<UserRole> userRoles) {
        if (userRoles == null) return Set.of();
        return userRoles.stream()
                .filter(ur -> ur != null && ur.getRole() != null && ur.getRole().getName() != null)
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());
    }


    @Named("mapDoctorSpecialty")
    default String mapDoctorSpecialty(Collection<UserRole> userRoles) {
        if (userRoles == null) return null;

        return userRoles.stream()
                .filter(ur -> ur != null && ur.getRole() != null && "DOCTOR".equalsIgnoreCase(ur.getRole().getName()))
                .map(ur -> ur.getSpecialty() != null ? ur.getSpecialty().getName() : null)
                .filter(specialtyName -> specialtyName != null)
                .findFirst()
                .orElse(null);
    }
}

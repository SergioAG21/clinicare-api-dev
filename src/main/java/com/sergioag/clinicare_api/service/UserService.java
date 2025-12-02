package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.dto.UpdateUserDTO;
import com.sergioag.clinicare_api.dto.UserResponseDTO;
import com.sergioag.clinicare_api.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    List<User> findAll();

//    Page<User> findAll(Pageable pageable);

    User findById(Long id);

    User save(User user);

    User getCurrentUser(Authentication authentication);

    User updateUserRoles(Long id, User userData);

    User makeUserIncomplete(Long id);

    void deleteById(Long id);

    Object update(Long id, UpdateUserDTO dto);

    void assignPatientToDoctor(Long patientId, Long doctorId);

    List<User> findDoctorBySpecialtyId(Long specialityId);
}

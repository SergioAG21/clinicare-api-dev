package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

//    Page<User> findAll(Pageable pageable);

    User findById(Long id);

    User save(User user);

    User getCurrentUser(Authentication authentication);

    User update(Long id, User user);

    User updateRoles(Long id, User newData, Long specialtyId);

    User updateRolesOnly(Long id, User userData);

    void deleteById(Long id);
}

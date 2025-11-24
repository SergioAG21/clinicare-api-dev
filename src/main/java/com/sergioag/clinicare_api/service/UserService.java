package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    List<User> findAll();

//    Page<User> findAll(Pageable pageable);

    User findById(Long id);

    User save(User user);

    User getCurrentUser(Authentication authentication);

    User update(Long id, User user);

    User updateUserRoles(Long id, User userData);

    void deleteById(Long id);
}

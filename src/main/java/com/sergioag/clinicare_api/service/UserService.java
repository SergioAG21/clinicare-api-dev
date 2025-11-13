package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

//    Page<User> findAll(Pageable pageable);
//
//    User findById(Long id);

    User save(User user);

//    Optional<User> update(Long id, User user);
//
//    void deleteById(Long id);

    Optional<User> findByEmailAndPassword(String email, String password);
}

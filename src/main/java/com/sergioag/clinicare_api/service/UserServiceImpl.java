package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.entity.User;
import com.sergioag.clinicare_api.exception.EmailNotFoundException;
import com.sergioag.clinicare_api.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("El usuario con email no está registrado o no está confirmado"));
    }

    public void deleteById(Long id) {
        id = this.findById(id).getId();

        userRepository.deleteById(id);
    }

}

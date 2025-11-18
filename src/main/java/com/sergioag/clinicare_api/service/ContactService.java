package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.entity.ContactMessage;

import java.util.List;
import java.util.Optional;

public interface ContactService {
    List<ContactMessage> findAll();

    Optional<ContactMessage> findById(Long id);

    ContactMessage save(ContactMessage contactMessage);

    void deleteById(Long id);
}

package com.sergioag.clinicare_api.service;

import com.sergioag.clinicare_api.entity.ContactMessage;
import com.sergioag.clinicare_api.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {
    private ContactMessageRepository contactMessageRepository;

    public ContactServiceImpl(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    @Override
    public List<ContactMessage> findAll() {
        return contactMessageRepository.findAll();
    }

    @Override
    public Optional<ContactMessage> findById(Long id) {
        return this.contactMessageRepository.findById(id);
    }

    @Override
    public ContactMessage save(ContactMessage message) {
        return contactMessageRepository.save(message);
    }

    @Override
    public void deleteById(Long id) {
        contactMessageRepository.deleteById(id);
    }

}

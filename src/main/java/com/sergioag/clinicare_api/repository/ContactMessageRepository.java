package com.sergioag.clinicare_api.repository;

import com.sergioag.clinicare_api.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}


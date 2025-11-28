package com.sergioag.clinicare_api.repository;

import com.sergioag.clinicare_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDni(String dni);

    @Query("""
    SELECT ur.user 
    FROM UserRole ur
    WHERE ur.role.id = :doctorRoleId
      AND ur.specialty.id = :specialtyId
    """)
    List<User> findDoctorsBySpecialty(Long specialtyId, Long doctorRoleId);
}

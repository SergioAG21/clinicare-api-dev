package com.sergioag.clinicare_api.repository;

import com.sergioag.clinicare_api.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.name = :roleName")
    Optional<UserRole> findByUserIdAndRoleName(@Param("userId") Long userId,
                                               @Param("roleName") String roleName);
}

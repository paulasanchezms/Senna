package com.senna.senna.Repository;

import com.senna.senna.Entity.User;
import com.senna.senna.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Buscar todos los usuarios por rol
    List<User> findByRole(Role role);

    // Buscar psicólogos por especialidad
    List<User> findByRoleAndSpecialtyContainingIgnoreCase(Role role, String specialty);
}
package com.senna.senna.Repository;

import com.senna.senna.Entity.ProfileStatus;
import com.senna.senna.Entity.User;
import com.senna.senna.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     */
    Optional<User> findByEmail(String email);

    /**
     * Lista todos los usuarios según su rol (por ejemplo, ROLE.PSYCHOLOGIST o ROLE.PATIENT).
     */
    List<User> findByRole(Role role);

    /**
     * Busca psicólogos por especialidad, ignorando mayúsculas y minúsculas.
     * Útil para filtros en búsquedas.
     */
    List<User> findByRoleAndProfileSpecialtyContainingIgnoreCase(Role role, String specialty);

    /**
     * Lista usuarios según su rol y el estado de su perfil profesional (por ejemplo, APROBADO o PENDING).
     */
    List<User> findByRoleAndProfile_Status(Role role, ProfileStatus status);

}
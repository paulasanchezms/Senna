package com.senna.senna.Repository;

import com.senna.senna.Entity.PsychologistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PsychologistProfileRepository extends JpaRepository<PsychologistProfile, Long> {
    /**
     * Recupera el perfil profesional a partir del userId.
     */
    Optional<PsychologistProfile> findByUserId(Long userId);

}

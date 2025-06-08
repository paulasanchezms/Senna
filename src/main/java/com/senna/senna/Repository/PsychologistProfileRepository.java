package com.senna.senna.Repository;

import com.senna.senna.Entity.PsychologistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PsychologistProfileRepository extends JpaRepository<PsychologistProfile, Long> {
    /**
     * Recupera el perfil profesional a partir del userId.
     */
    Optional<PsychologistProfile> findByUserId(Long userId);

    /**
     * Recupera el perfil junto con las workingHours
     */
    @Query("SELECT p FROM PsychologistProfile p LEFT JOIN FETCH p.workingHours WHERE p.user.id = :userId")
    Optional<PsychologistProfile> findByUserIdWithWorkingHours(@Param("userId") Long userId);

}

package com.senna.senna.Repository;

import com.senna.senna.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Devuelve todas las valoraciones asociadas a un psicólogo por su ID.
     */
    List<Review> findByPsychologist_Id(Long psychologistId);

    /**
     * Busca una valoración específica realizada por un paciente a un psicólogo.
     * Útil para prevenir valoraciones duplicadas.
     */
    Optional<Review> findByPatient_IdAndPsychologist_Id(Long patientId, Long psychologistId);
}
package com.senna.senna.Repository;

import com.senna.senna.Entity.WorkingHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkingHourRepository extends JpaRepository<WorkingHour, Long> {
    /**
     * Encuentra todas las franjas asociadas al perfil de un psicólogo (mediante userId).
     */
    List<WorkingHour> findByProfileUserId(Long userId);

    /**
     * Devuelve las franjas horarias de un psicólogo para un día de la semana específico.
     */
    List<WorkingHour> findByProfileUserIdAndDayOfWeek(Long userId, Integer dayOfWeek);

    /**
     * Elimina todas las franjas horarias de un psicólogo por su ID de usuario.
     */
    void deleteByProfileUserId(Long userId);
}
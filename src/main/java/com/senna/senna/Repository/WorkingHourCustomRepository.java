package com.senna.senna.Repository;

import com.senna.senna.Entity.WorkingHourCustom;
import com.senna.senna.Entity.PsychologistProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkingHourCustomRepository extends JpaRepository<WorkingHourCustom, Long> {

    /**
     * Obtiene todas las franjas horarias personalizadas para un perfil y una fecha concretos.
     *
     * @param profile el perfil del psicólogo
     * @param date la fecha específica
     * @return lista de franjas horarias personalizadas
     */
    List<WorkingHourCustom> findByProfileAndDate(PsychologistProfile profile, LocalDate date);

    /**
     * Elimina todas las franjas horarias personalizadas para un perfil y una fecha concretos.
     *
     * @param profile el perfil del psicólogo
     * @param date la fecha específica
     */
    void deleteByProfileAndDate(PsychologistProfile profile, LocalDate date);
}
package com.senna.senna.Repository;

import com.senna.senna.Entity.WorkingHourCustom;
import com.senna.senna.Entity.PsychologistProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkingHourCustomRepository extends JpaRepository<WorkingHourCustom, Long> {

    List<WorkingHourCustom> findByProfileAndDate(PsychologistProfile profile, LocalDate date);

    List<WorkingHourCustom> findByProfile(PsychologistProfile profile);

    void deleteByProfileAndDate(PsychologistProfile profile, LocalDate date);
}
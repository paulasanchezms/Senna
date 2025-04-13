package com.senna.senna.Repository;

import com.senna.senna.Entity.Appointment;
import com.senna.senna.Entity.AppointmentStatus;
import com.senna.senna.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Citas de un paciente
    List<Appointment> findByPatient(User patient);

    // Citas de un psicólogo
    List<Appointment> findByPsychologist(User psychologist);

    List<Appointment> findByPsychologistAndDateTimeBetweenAndStatusNot(
            User psychologist,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );
}
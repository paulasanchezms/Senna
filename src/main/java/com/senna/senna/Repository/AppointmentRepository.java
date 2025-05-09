// src/main/java/com/senna/senna/Repository/AppointmentRepository.java
package com.senna.senna.Repository;

import com.senna.senna.Entity.Appointment;
import com.senna.senna.Entity.AppointmentStatus;
import com.senna.senna.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /** Todas las citas de un paciente */
    List<Appointment> findByPatient(User patient);

    /** Todas las citas de un psicólogo */
    List<Appointment> findByPsychologist(User psychologist);

    /** Citas de un psicólogo en un rango (historial) */
    List<Appointment> findByPsychologistAndDateTimeBetween(
            User psychologist,
            LocalDateTime start,
            LocalDateTime end
    );

    /** Citas de un psicólogo en un rango excluyendo un estado (p.ej. CANCELADA) */
    List<Appointment> findByPsychologistAndDateTimeBetweenAndStatusNot(
            User psychologist,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );

    List<Appointment> findByPsychologistAndStatus(User psychologist, AppointmentStatus status);
}
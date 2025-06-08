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

    /**
     * Devuelve todas las citas de un psicólogo con un estado específico (pendiente, confirmada, etc.).
     */
    List<Appointment> findByPsychologistAndStatus(User psychologist, AppointmentStatus status);

    /**
     * Devuelve todas las citas de un psicólogo entre dos fechas, usando su ID en lugar del objeto.
     */
    List<Appointment> findByPsychologistIdAndDateTimeBetween(Long psychologistId, LocalDateTime start, LocalDateTime end);

    /**
     * Comprueba si ya existe una cita entre un paciente y un psicólogo en un horario específico
     * y con un estado determinado (por ejemplo, pendiente o confirmada).
     */
    boolean existsByPsychologistAndPatientAndDateTimeAndStatusIn(
            User psychologist,
            User patient,
            LocalDateTime dateTime,
            List<AppointmentStatus> statuses
    );

    /**
     * Devuelve todas las citas entre un paciente y un psicólogo que estén en alguno de los estados dados.
     * Útil para verificar historial de relaciones entre ambos.
     */
    List<Appointment> findByPatient_IdAndPsychologist_IdAndStatusIn(
            Long patientId,
            Long psychologistId,
            List<AppointmentStatus> statuses
    );
}
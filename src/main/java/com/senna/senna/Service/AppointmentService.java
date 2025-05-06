package com.senna.senna.Service;

import com.senna.senna.DTO.CreateAppointmentDTO;
import com.senna.senna.DTO.AppointmentResponseDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para gestión de citas.
 */
public interface AppointmentService {
    /**
     * Programa una nueva cita.
     */
    AppointmentResponseDTO scheduleAppointment(CreateAppointmentDTO dto);

    /**
     * Actualiza una cita existente.
     */
    AppointmentResponseDTO updateAppointment(Long appointmentId, CreateAppointmentDTO dto);

    /**
     * Cancela una cita (cambia su estado a CANCELADA).
     */
    void cancelAppointment(Long appointmentId);

    /**
     * Obtiene las citas de un paciente por su ID.
     */
    List<AppointmentResponseDTO> getAppointmentsForPatient(Long patientId);

    /**
     * Obtiene las citas de un psicólogo por su ID.
     */
    List<AppointmentResponseDTO> getAppointmentsForPsychologist(Long psychologistId);

    /**
     * Lista horas disponibles para un psicólogo en una fecha dada.
     */
    List<String> getAvailableTimes(Long psychologistId, LocalDate date);
}
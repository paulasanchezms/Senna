package com.senna.senna.DTO;

import com.senna.senna.Entity.AppointmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long patientId;            // ID del paciente que reserva
    private Long psychologistId;       // ID del psicólogo asignado
    private LocalDateTime dateTime;    // Fecha y hora de la cita
    private Integer duration;          // Duración en minutos
    private String description;        // Descripción opcional
    private AppointmentStatus status;  // Estado de la cita (p.ej. PENDIENTE, CONFIRMADA, CANCELADA)
}
package com.senna.senna.DTO;

import com.senna.senna.Entity.AppointmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private LocalDateTime dateTime;
    private Integer duration;
    private AppointmentStatus status; // En creación quizá se setee por defecto a PENDIENTE
    private String description;
    private Long psychologistId;  // Al registrar, el paciente elige o el sistema asigna un psicólogo.
    // Opcional: puedes agregar otros campos si es necesario.
}
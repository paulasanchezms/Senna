package com.senna.senna.DTO;

import com.senna.senna.Entity.AppointmentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long patientId;
    private Long psychologistId;
    private LocalDateTime dateTime;
    private Integer duration;
    private String description;
    private AppointmentStatus status;
}
package com.senna.senna.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateAppointmentDTO {
    private Long patientId;
    private Long psychologistId;
    private LocalDateTime dateTime;
    private Integer duration;
    private String description;
}
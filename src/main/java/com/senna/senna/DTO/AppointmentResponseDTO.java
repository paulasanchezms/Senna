package com.senna.senna.DTO;

import com.senna.senna.Entity.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponseDTO {
    private Long id;
    private LocalDateTime dateTime;
    private Integer duration;
    private AppointmentStatus status;
    private String description;
    // Puedes incluir información resumida del paciente y del psicólogo, utilizando DTOs.
    private UserResponseDTO patient;
    private UserResponseDTO psychologist;
}
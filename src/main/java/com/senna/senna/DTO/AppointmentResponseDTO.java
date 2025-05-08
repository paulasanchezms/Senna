// AppointmentResponseDTO.java
package com.senna.senna.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentResponseDTO {
    private Long id;
    private UserResponseDTO patient;
    private Long patientId;
    private Long psychologistId;
    private LocalDateTime dateTime;
    private Integer duration;
    private String status;
    private String description;
}
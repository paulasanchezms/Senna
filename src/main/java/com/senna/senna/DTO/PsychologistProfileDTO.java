package com.senna.senna.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PsychologistProfileDTO {
    private Long id;
    private Long userId;
    private Integer consultationDuration; // minutos
    private BigDecimal consultationPrice;
    private String specialty;
    private String location;
    private List<WorkingHourDTO> workingHours;
}
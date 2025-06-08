package com.senna.senna.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreatePsychologistProfileDTO {
    private Integer consultationDuration;
    private BigDecimal consultationPrice;
    private String specialty;
    private String location;
    private String document;
    private List<WorkingHourDTO> workingHours;
}
// UpdatePsychologistProfileDTO.java
package com.senna.senna.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdatePsychologistProfileDTO {
    private Integer consultationDuration;
    private BigDecimal consultationPrice;
    private String specialty;
    private String location;
}
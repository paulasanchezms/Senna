package com.senna.senna.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class DiaryEntryResponseDTO {
    private Long id;
    private LocalDate date;
    private List<MoodDTO> mood;
    private List<SymptomDTO> symptoms;
    private String notes;
    private UserResponseDTO user;
    private Integer moodLevel;
}
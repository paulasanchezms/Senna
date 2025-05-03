package com.senna.senna.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DiaryEntryDTO {
    private List<Long> symptomIds;
    private List<Long> moodIds;
    private String notes;
    private LocalDate date;
    private Integer moodLevel;
}
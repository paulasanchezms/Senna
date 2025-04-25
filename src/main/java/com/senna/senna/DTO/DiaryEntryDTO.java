package com.senna.senna.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DiaryEntryDTO {
    private LocalDate date;
    private String mood;
    private String symptoms;
    private String notes;
}
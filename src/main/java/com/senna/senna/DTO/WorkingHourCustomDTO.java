package com.senna.senna.DTO;

import lombok.Data;

@Data
public class WorkingHourCustomDTO {
    private Long id;
    private Long profileId;
    private String date; // formato ISO '2025-05-21'
    private String startTime; // 'HH:mm'
    private String endTime;   // 'HH:mm'
}
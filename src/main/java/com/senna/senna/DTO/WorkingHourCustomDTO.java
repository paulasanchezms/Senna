package com.senna.senna.DTO;

import lombok.Data;

@Data
public class WorkingHourCustomDTO {
    private Long id;
    private String date;
    private String startTime;
    private String endTime;
}
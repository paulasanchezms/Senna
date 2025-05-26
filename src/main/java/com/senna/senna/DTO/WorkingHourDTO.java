// WorkingHourDTO.java
package com.senna.senna.DTO;

import lombok.Data;

@Data
public class WorkingHourDTO {
    private Integer dayOfWeek;
    private String startTime;
    private String endTime;
}
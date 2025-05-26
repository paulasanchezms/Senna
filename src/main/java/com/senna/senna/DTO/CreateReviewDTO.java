package com.senna.senna.DTO;

import lombok.Data;

@Data
public class CreateReviewDTO {
    private Long psychologistId;
    private Integer rating;       // de 1 a 5
    private String comment;       // texto libre
}
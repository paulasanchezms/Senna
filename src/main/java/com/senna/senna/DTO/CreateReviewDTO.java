package com.senna.senna.DTO;

import lombok.Data;

@Data
public class CreateReviewDTO {
    private Long psychologistId;
    private Integer rating;
    private String comment;
}
package com.senna.senna.DTO;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private String patientName;
    private String patientPhoto;
    private Integer rating;
    private String comment;
    private String createdAt;
}
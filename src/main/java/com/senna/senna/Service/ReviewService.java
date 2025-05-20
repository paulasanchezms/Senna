package com.senna.senna.Service;

import com.senna.senna.DTO.CreateReviewDTO;
import com.senna.senna.DTO.ReviewDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO saveReview(String patientEmail, CreateReviewDTO dto);
    List<ReviewDTO> getReviewsForPsychologist(Long psychologistId);
}
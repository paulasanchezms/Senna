package com.senna.senna.Mapper;

import com.senna.senna.DTO.ReviewDTO;
import com.senna.senna.Entity.Review;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dto.setPatientName(review.getPatient().getName() + " " + review.getPatient().getLastName());
        dto.setPatientPhoto(review.getPatient().getPhotoUrl()); // puede ser null
        return dto;
    }
}
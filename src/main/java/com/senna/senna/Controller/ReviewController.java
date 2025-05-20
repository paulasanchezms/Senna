package com.senna.senna.Controller;

import com.senna.senna.DTO.CreateReviewDTO;
import com.senna.senna.DTO.ReviewDTO;
import com.senna.senna.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody CreateReviewDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername(); // email del paciente autenticado
        ReviewDTO review = reviewService.saveReview(email, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/psychologist/{id}")
    public ResponseEntity<List<ReviewDTO>> getReviewsForPsychologist(@PathVariable Long id) {
        List<ReviewDTO> reviews = reviewService.getReviewsForPsychologist(id);
        return ResponseEntity.ok(reviews);
    }
}
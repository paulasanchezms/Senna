package com.senna.senna.Service;

import com.senna.senna.DTO.CreateReviewDTO;
import com.senna.senna.DTO.ReviewDTO;
import com.senna.senna.Entity.Review;
import com.senna.senna.Entity.User;
import com.senna.senna.Mapper.ReviewMapper;
import com.senna.senna.Repository.ReviewRepository;
import com.senna.senna.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    /**
     * Guarda una nueva reseña realizada por un paciente hacia un psicólogo.
     * Verifica que no exista una reseña previa entre los mismos usuarios.
     */
    @Override
    public ReviewDTO saveReview(String patientEmail, CreateReviewDTO dto) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        User psychologist = userRepository.findById(dto.getPsychologistId())
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        reviewRepository.findByPatient_IdAndPsychologist_Id(patient.getId(), psychologist.getId())
                .ifPresent(r -> { throw new RuntimeException("Ya has valorado a este profesional."); });

        Review review = new Review();
        review.setPatient(patient);
        review.setPsychologist(psychologist);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return reviewMapper.toDTO(saved);
    }

    /**
     * Devuelve todas las reseñas asociadas a un psicólogo concreto.
     */
    @Override
    public List<ReviewDTO> getReviewsForPsychologist(Long psychologistId) {
        return reviewRepository.findByPsychologist_Id(psychologistId)
                .stream()
                .map(reviewMapper::toDTO)
                .toList();
    }
}
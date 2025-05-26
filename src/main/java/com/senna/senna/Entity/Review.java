package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"patient_id", "psychologist_id"})
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Paciente que hace la valoración
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Psicólogo valorado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psychologist_id", nullable = false)
    private User psychologist;

    @Column(nullable = false)
    private int rating; // 1-5 estrellas

    @Column(length = 500)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
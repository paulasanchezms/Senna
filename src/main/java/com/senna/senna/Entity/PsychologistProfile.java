package com.senna.senna.Entity;

import com.senna.senna.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "psychologist_profile")
public class PsychologistProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "consultation_duration", nullable = false)
    private Integer consultationDuration; // en minutos

    @Column(name = "consultation_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal consultationPrice;

    @Column(length = 150, nullable = true)
    private String specialty;

    @Column(length = 150, nullable = true)
    private String location;

    @Column(length = 255)
    private String document;

    @Column
    private String description;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkingHour> workingHours;
}


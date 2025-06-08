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

    // Relación uno a uno con el usuario (debe tener rol PSYCHOLOGIST)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Duración de las consultas en minutos
    @Column(name = "consultation_duration", nullable = false)
    private Integer consultationDuration;

    // Precio de la consulta
    @Column(name = "consultation_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal consultationPrice;

    // Especialidad del profesional
    @Column(length = 150, nullable = true)
    private String specialty;

    // Ubicación
    @Column(length = 150, nullable = true)
    private String location;

    //Documento acreditativo
    @Column(length = 255)
    private String document;

    //Descripción libre del perfil
    @Column
    private String description;

    // Lista de horarios por defecto definidos por el psicólogo
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkingHour> workingHours;

    // Estado del perfil
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProfileStatus status = ProfileStatus.PENDING;
}


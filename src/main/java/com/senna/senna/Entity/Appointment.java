package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el paciente (usuario con rol PATIENT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Relación con el psicólogo (usuario con rol PSYCHOLOGIST)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psychologist_id", nullable = false)
    private User psychologist;

    // Fecha y hora de la cita
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    // Duración de la cita en minutos
    @Column(nullable = false)
    private Integer duration;

    // Estado de la cita: PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AppointmentStatus status;

    // Descripción opcional de la cita
    @Column(length = 500)
    private String description;
}
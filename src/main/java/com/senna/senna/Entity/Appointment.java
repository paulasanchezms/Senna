package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    // Por ejemplo, en minutos o como un campo específico
    private Integer duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;  // Por ejemplo, PENDIENTE, CONFIRMADA, CANCELADA

    private String description;

    // Paciente que reserva la cita.
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Psicólogo asignado a la cita.
    @ManyToOne
    @JoinColumn(name = "psychologist_id", nullable = false)
    private User psychologist;
}
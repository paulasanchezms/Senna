package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "working_hours_custom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkingHourCustom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private PsychologistProfile profile;

    @Column(nullable = false)
    private LocalDate date; // Fecha concreta

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
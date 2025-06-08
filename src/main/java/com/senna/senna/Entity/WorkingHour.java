package com.senna.senna.Entity;

import com.senna.senna.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "psychologist_working_hours")
public class WorkingHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el perfil del psicólogo al que pertenece este horario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private PsychologistProfile profile;

    // Día de la semana
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    // Hora de inicio de disponibilidad
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Hora de fin de disponibilidad
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}

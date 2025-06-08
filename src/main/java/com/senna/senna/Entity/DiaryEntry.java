package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "diary_entries")
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha de la entrada en el diario
    private LocalDate date;

    // Relación N:M con síntomas (puede haber múltiples síntomas por entrada)
    @ManyToMany
    @JoinTable(name = "diary_entry_symptoms",
            joinColumns = @JoinColumn(name = "diary_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "symptom_id"))
    private Set<Symptom> symptoms;

    // Relación N:M con emociones/estados de ánimo (puede haber varios por entrada)
    @ManyToMany
    @JoinTable(name = "diary_entry_moods",
            joinColumns = @JoinColumn(name = "diary_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "mood_id"))
    private Set<Mood> moods;

    // Notas adicionales escritas por el paciente
    private String notes;

    // Usuario asociado a la entrada (debe ser un paciente)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Nivel general de estado de ánimo en formato numérico
    private Integer moodLevel;
}


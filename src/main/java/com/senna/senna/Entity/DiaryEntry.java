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

        private LocalDate date;

        @ManyToMany
        @JoinTable(name = "diary_entry_symptoms",
                joinColumns = @JoinColumn(name = "diary_entry_id"),
                inverseJoinColumns = @JoinColumn(name = "symptom_id"))
        private Set<Symptom> symptoms;

        @ManyToMany
        @JoinTable(name = "diary_entry_moods",
                joinColumns = @JoinColumn(name = "diary_entry_id"),
                inverseJoinColumns = @JoinColumn(name = "mood_id"))
        private Set<Mood> moods;

        private String notes; // Texto libre

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user; // Solo pacientes pueden tener entradas
    }


package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

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

        private String mood; // ej. Feliz, Triste, Ansioso...

        private String symptoms; // ej. Dolor de cabeza, insomnio...

        private String notes; // Texto libre

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user; // Solo pacientes pueden tener entradas
    }


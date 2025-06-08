package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "moods")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del estado de ánimo
    private String name;

    // Nombre del icono asociado
    private String icon;
}
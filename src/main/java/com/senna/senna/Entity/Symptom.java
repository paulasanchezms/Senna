package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "symptoms")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Nombre
    private String name;

    //Icono
    private String icon;
}
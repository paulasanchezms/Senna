package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_patient")
    private Long id_patient;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "psychologist_patient", // Name of the join table
            joinColumns = @JoinColumn(name = "id_patient"), // Foreign key for Patient
            inverseJoinColumns = @JoinColumn(name = "dni_psychologist") // Foreign key for Psychologist
    )
    private Set<Psychologist> psychologists; // Relationship with psychologists
}

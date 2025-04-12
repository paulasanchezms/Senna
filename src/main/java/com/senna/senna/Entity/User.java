package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"patients", "psychologists"})
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id_user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String last_name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Campos exclusivos del rol PSYCHOLOGIST
    private String dni; // Solo para psicólogos
    private String qualification;
    private String specialty;
    private String location;
    private String document;

    // Relaciones con pacientes (solo si este usuario es un psicólogo)
    @ManyToMany
    @JoinTable(
            name = "psychologist_patient",
            joinColumns = @JoinColumn(name = "id_psychologist"),
            inverseJoinColumns = @JoinColumn(name = "id_patient")
    )
    private Set<User> patients; // Solo para psicólogo

    // Relaciones con psicólogos (solo si este usuario es un paciente)
    @ManyToMany(mappedBy = "patients")
    private Set<User> psychologists; // Solo para paciente
}
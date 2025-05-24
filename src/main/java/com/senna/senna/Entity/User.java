package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Usuario básico de la aplicación. Solo datos personales y credenciales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"profile", "patients", "psychologists"})
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Perfil profesional (si role = PSYCHOLOGIST)
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private PsychologistProfile profile;

    /**
     * Relación paciente–psicólogo: solo si este usuario es psicólogo
     */
    @ManyToMany
    @JoinTable(
            name = "psychologist_patient",
            joinColumns = @JoinColumn(name = "id_psychologist"),
            inverseJoinColumns = @JoinColumn(name = "id_patient")
    )
    @ToString.Exclude
    private List<User> patients;

    /**
     * Relación psicólogo–paciente: solo si este usuario es paciente
     */
    @ManyToMany(mappedBy = "patients")
    @ToString.Exclude
    private List<User> psychologists;

    @OneToMany(mappedBy = "psychologist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> receivedReviews;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> givenReviews;

    @Column(name = "active")
    private boolean active;

    @Column(name = "terms_accepted")
    private boolean termsAccepted;
}


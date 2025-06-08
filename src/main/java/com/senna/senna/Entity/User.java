package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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

    // Nombre del usuario
    @Column(name = "name", nullable = false)
    private String name;

    // Apellidos del usuario
    @Column(name = "last_name", nullable = false)
    private String lastName;

    // Email único, usado también como nombre de usuario
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Contraseña encriptada
    @Column(name = "password", nullable = false)
    private String password;

    // Teléfono de contacto (opcional)
    @Column(name = "phone")
    private String phone;

    // URL de la foto de perfil (opcional)
    @Column(name = "photo_url")
    private String photoUrl;

    // Rol del usuario: ADMIN, PATIENT o PSYCHOLOGIST
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Perfil profesional asociado si es psicólogo
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private PsychologistProfile profile;

    // Lista de pacientes asignados a este usuario si es psicólogo
    @ManyToMany
    @JoinTable(
            name = "psychologist_patient",
            joinColumns = @JoinColumn(name = "id_psychologist"),
            inverseJoinColumns = @JoinColumn(name = "id_patient")
    )
    @ToString.Exclude
    private List<User> patients;

    // Lista de psicólogos asignados a este usuario si es paciente
    @ManyToMany(mappedBy = "patients")
    @ToString.Exclude
    private List<User> psychologists;

    // Reseñas recibidas si el usuario es psicólogo
    @OneToMany(mappedBy = "psychologist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> receivedReviews;

    // Reseñas escritas si el usuario es paciente
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> givenReviews;

    // Estado activo del usuario (por ejemplo, si está baneado o no)
    @Column(name = "active")
    private boolean active;

    // Indica si el usuario ha aceptado los términos y condiciones
    @Column(name = "terms_accepted")
    private boolean termsAccepted;
}


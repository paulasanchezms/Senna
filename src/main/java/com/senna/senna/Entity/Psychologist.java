package com.senna.senna.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "psychologists")
public class Psychologist {
    @Id
    @Column(name = "dni")
    private String dni;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "qualification")
    @NonNull
    private String qualification; // Professional qualification

    @Column(name = "specialty")
    @NonNull
    private String specialty; // Specialization area

    @Column(name = "location")
    @NonNull
    private String location; // Location or address

    @Column(name = "document")
    @NonNull
    private String document; // Additional document (e.g., CV or license)

    @ManyToMany(mappedBy = "psychologists")
    private Set<Patient> patients;


}

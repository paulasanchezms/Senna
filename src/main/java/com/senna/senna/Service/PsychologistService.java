package com.senna.senna.Service;

import com.senna.senna.Entity.Psychologist;
import com.senna.senna.Repository.PsychologistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PsychologistService {

    @Autowired
    private PsychologistRepository psychologistRepository;

    // Crear un nuevo psicólogo
    public Psychologist createPsychologist(Psychologist psychologist) {
        return psychologistRepository.save(psychologist);
    }

    // Obtener todos los psicólogos
    public List<Psychologist> getAllPsychologists() {
        return psychologistRepository.findAll();
    }

    // Obtener un psicólogo por DNI
    public Optional<Psychologist> getPsychologistByDni(String dni) {
        return psychologistRepository.findById(dni);
    }

    // Actualizar un psicólogo
    public Psychologist updatePsychologist(String dni, Psychologist updatedPsychologist) {
        return psychologistRepository.findById(dni)
                .map(psychologist -> {
                    psychologist.setQualification(updatedPsychologist.getQualification());
                    psychologist.setSpecialty(updatedPsychologist.getSpecialty());
                    psychologist.setLocation(updatedPsychologist.getLocation());
                    psychologist.setDocument(updatedPsychologist.getDocument());
                    return psychologistRepository.save(psychologist);
                })
                .orElseThrow(() -> new RuntimeException("Psychologist not found with DNI: " + dni));
    }

    // Eliminar un psicólogo
    public void deletePsychologist(String dni) {
        psychologistRepository.deleteById(dni);
    }
}

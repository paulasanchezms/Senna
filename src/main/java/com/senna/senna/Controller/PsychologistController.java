package com.senna.senna.Controller;


import com.senna.senna.Entity.Psychologist;
import com.senna.senna.Service.PsychologistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psychologists")
public class PsychologistController {

    @Autowired
    private PsychologistService psychologistService;

    // Crear un psicólogo
    @PostMapping
    public ResponseEntity<Psychologist> createPsychologist(@RequestBody  Psychologist psychologist) {
        return ResponseEntity.ok(psychologistService.createPsychologist(psychologist));
    }

    // Obtener todos los psicólogos
    @GetMapping
    public ResponseEntity<List<Psychologist>> getAllPsychologists() {
        return ResponseEntity.ok(psychologistService.getAllPsychologists());
    }

    // Obtener un psicólogo por DNI
    @GetMapping("/{dni}")
    public ResponseEntity<Psychologist> getPsychologistByDni(@PathVariable String dni) {
        return psychologistService.getPsychologistByDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar un psicólogo
    @PutMapping("/{dni}")
    public ResponseEntity<Psychologist> updatePsychologist(@PathVariable String dni, @RequestBody Psychologist updatedPsychologist) {
        return ResponseEntity.ok(psychologistService.updatePsychologist(dni, updatedPsychologist));
    }

    // Eliminar un psicólogo
    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> deletePsychologist(@PathVariable String dni) {
        psychologistService.deletePsychologist(dni);
        return ResponseEntity.noContent().build();
    }
}
package com.senna.senna.Controller;

import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Crear usuario
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserDTO dto) {
        UserResponseDTO created = userService.createUser(dto);
        return ResponseEntity.ok(created);
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Obtener por ID
    @GetMapping("/by-id/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Obtener por email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        Optional<UserResponseDTO> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener todos los psicólogos
    @GetMapping("/psychologists")
    public ResponseEntity<List<UserResponseDTO>> getAllPsychologists() {
        List<UserResponseDTO> psychologists = userService.getAllPsychologists();
        return ResponseEntity.ok(psychologists);
    }

    // Buscar psicólogos por especialidad
    @GetMapping("/psychologists/search")
    public ResponseEntity<List<UserResponseDTO>> searchPsychologistsBySpecialty(@RequestParam String specialty) {
        List<UserResponseDTO> result = userService.getPsychologistsBySpecialty(specialty);
        return ResponseEntity.ok(result);
    }

    // Obtener todos los pacientes
    @GetMapping("/patients")
    public ResponseEntity<List<UserResponseDTO>> getAllPatients() {
        List<UserResponseDTO> patients = userService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // Asignar paciente a psicólogo (nuevo mapping para evitar colisión)
    @PostMapping("/assign-patient")
    public ResponseEntity<String> assignPatientToPsychologist(@RequestParam Long psychologistId, @RequestParam Long patientId) {
        userService.assignPatientToPsychologist(psychologistId, patientId);
        return ResponseEntity.ok("Asignación realizada con éxito.");
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        UserResponseDTO updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    //Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }
}
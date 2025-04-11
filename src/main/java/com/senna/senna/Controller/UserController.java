package com.senna.senna.Controller;

import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.User;
import com.senna.senna.Mapper.UserMapper;
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
    public ResponseEntity<User> createUser(@RequestBody CreateUserDTO dto) {
        User user = UserMapper.fromDTO(dto);
        User created = userService.createUser(user);
        return ResponseEntity.ok(created);
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toResponseDTO(user));
    }

    // Obtener por email
    @GetMapping("/email/{email}")
    public ResponseEntity<Optional<User>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // Obtener todos los psicólogos
    @GetMapping("/psychologists")
    public ResponseEntity<List<User>> getAllPsychologists() {
        return ResponseEntity.ok(userService.getAllPsychologists());
    }

    // Buscar psicólogos por especialidad
    @GetMapping("/psychologists/search")
    public ResponseEntity<List<User>> searchPsychologistsBySpecialty(@RequestParam String specialty) {
        return ResponseEntity.ok(userService.getPsychologistsBySpecialty(specialty));
    }

    // Obtener todos los pacientes
    @GetMapping("/patients")
    public ResponseEntity<List<User>> getAllPatients() {
        return ResponseEntity.ok(userService.getAllPatients());
    }

    // Asignar paciente a psicólogo
    @PostMapping("/assign")
    public ResponseEntity<String> assignPatientToPsychologist(@RequestParam Long psychologistId, @RequestParam Long patientId) {
        userService.assignPatientToPsychologist(psychologistId, patientId);
        return ResponseEntity.ok("Asignación realizada con éxito.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        User updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(UserMapper.toResponseDTO(updated));
    }
}
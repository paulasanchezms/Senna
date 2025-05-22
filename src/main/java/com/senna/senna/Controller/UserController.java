package com.senna.senna.Controller;

import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;

    /** Crea un usuario (paciente o psicólogo). No incluye perfil profesional */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserDTO dto) {
        UserResponseDTO created = userServiceImpl.createUser(dto);
        return ResponseEntity.ok(created);
    }

    /** Listado completo de usuarios (sin datos de perfil) */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userServiceImpl.getAllUsers());
    }

    /** Lista todos los usuarios con rol PSYCHOLOGIST */
    @GetMapping("/psychologists")
    public ResponseEntity<List<UserResponseDTO>> getAllPsychologists() {
        return ResponseEntity.ok(userServiceImpl.getAllPsychologists());
    }

    /** Lista todos los usuarios con rol PATIENT */
    @GetMapping("/patients")
    public ResponseEntity<List<UserResponseDTO>> getAllPatients() {
        return ResponseEntity.ok(userServiceImpl.getAllPatients());
    }

    /** Consulta un usuario por su ID */
    @GetMapping("/by-id/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userServiceImpl.getUserById(id));
    }

    /** Consulta un usuario por email */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userServiceImpl.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Actualiza campos básicos de un usuario (sin tocar perfil profesional) */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO dto
    ) {
        return ResponseEntity.ok(userServiceImpl.updateUser(id, dto));
    }

    /** Elimina un usuario */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userServiceImpl.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /** Asigna un paciente a un psicólogo (sigue aquí si quieres mantener esta lógica) */
    @PostMapping("/{psychologistId}/patients/{patientId}")
    public ResponseEntity<Void> assignPatientToPsychologist(
            @PathVariable Long psychologistId,
            @PathVariable Long patientId
    ) {
        userServiceImpl.assignPatientToPsychologist(psychologistId, patientId);
        return ResponseEntity.ok().build();
    }

    /** Lista todos los psicólogos filtrados por especialidad */
    @GetMapping("/psychologists/search")
    public ResponseEntity<List<UserResponseDTO>> getPsychologistsBySpecialty(
            @RequestParam String specialty
    ) {
        return ResponseEntity.ok(userServiceImpl.getPsychologistsBySpecialty(specialty));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails
    ) {
        return userServiceImpl.getUserByEmail(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        String imageUrl = userServiceImpl.uploadImageToImgBB(image);
        return ResponseEntity.ok().body(Map.of("url", imageUrl));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateCurrentUser(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @RequestBody UpdateUserDTO dto
    ) {
        userServiceImpl.updateUserByEmail(userDetails.getUsername(), dto);
        return ResponseEntity.ok().build();
    }
}
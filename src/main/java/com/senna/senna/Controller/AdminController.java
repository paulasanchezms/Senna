package com.senna.senna.Controller;

import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.Role;
import com.senna.senna.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final UserService userService;

    // Devuelve una lista de psicólogos que aún no han sido aprobados
    @GetMapping("/pending-psychologists")
    public List<UserResponseDTO> getPendingPsychologists() {
        return userService.findPendingPsychologists();
    }

    // Aprueba un psicólogo por su ID
    @PutMapping("/approve-psychologist/{id}")
    public ResponseEntity<String> approvePsychologist(@PathVariable Long id) {
        userService.approvePsychologist(id);
        return ResponseEntity.ok("Perfil de psicólogo aprobado correctamente.");
    }

    // Rechaza un psicólogo por su ID
    @PutMapping("/reject-psychologist/{id}")
    public ResponseEntity<String> rejectPsychologist(@PathVariable Long id) {
        userService.rejectPsychologist(id);
        return ResponseEntity.ok("Perfil de psicólogo rechazado correctamente.");
    }

    // Banea cualquier usuario por su ID
    @PutMapping("/ban-user/{id}")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok("Usuario baneado correctamente.");
    }

    // Devuelve todos los usuarios activos, excepto los administradores
    @GetMapping("/users")
    public List<UserResponseDTO> getAllActiveUsers() {
        return userService.getAllUsers().stream()
                .filter(user -> user.isActive() && user.getRole() != Role.ADMIN)
                .toList();
    }
}
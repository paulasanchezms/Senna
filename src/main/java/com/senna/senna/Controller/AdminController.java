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

    @GetMapping("/pending-psychologists")
    public List<UserResponseDTO> getPendingPsychologists() {
        return userService.findPendingPsychologists();
    }

    @PutMapping("/approve-psychologist/{id}")
    public ResponseEntity<String> approvePsychologist(@PathVariable Long id) {
        userService.approvePsychologist(id);
        return ResponseEntity.ok("Perfil de psicólogo aprobado correctamente.");
    }

    @PutMapping("/reject-psychologist/{id}")
    public ResponseEntity<String> rejectPsychologist(@PathVariable Long id) {
        userService.rejectPsychologist(id);
        return ResponseEntity.ok("Perfil de psicólogo rechazado correctamente.");
    }

    @PutMapping("/ban-user/{id}")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok("Usuario baneado correctamente.");
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllActiveUsers() {
        return userService.getAllUsers().stream()
                .filter(user -> user.isActive() && user.getRole() != Role.ADMIN)
                .toList();
    }
}
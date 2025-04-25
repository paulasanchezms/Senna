package com.senna.senna.Controller;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> register(


            @RequestPart("name") String name,
            @RequestPart("last_name") String lastName,
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("role") String role,
            @RequestPart(value = "dni", required = false) String dni,
            @RequestPart(value = "qualification", required = false) String qualification,
            @RequestPart(value = "specialty", required = false) String specialty,
            @RequestPart(value = "location", required = false) String location,
            @RequestPart(value = "document", required = false) MultipartFile document

    ) throws Exception {

        CreateUserDTO dto = new CreateUserDTO();
        dto.setName(name);
        dto.setLast_name(lastName);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(com.senna.senna.Entity.Role.valueOf(role));

        if (dto.getRole() == com.senna.senna.Entity.Role.PSYCHOLOGIST) {
            dto.setDni(dni);
            dto.setQualification(qualification);
            dto.setSpecialty(specialty);
            dto.setLocation(location);
            // El documento se asignará dentro del servicio si es válido
        }

        AuthResponse response = authService.register(dto, document);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) throws Exception {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }
}

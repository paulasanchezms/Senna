package com.senna.senna.Controller;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreatePsychologistProfileDTO;
import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.Entity.Role;
import com.senna.senna.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un nuevo usuario (paciente o psicólogo) y devuelve un JWT.
     * El CreateUserDTO debe incluir todos los campos necesarios.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody CreateUserDTO dto
    ) throws Exception {
        AuthResponse response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/register/psychologist",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AuthResponse> registerPsychologist(
            @Valid @RequestPart("user") CreateUserDTO userDto,
            @Valid @RequestPart("profile") CreatePsychologistProfileDTO profileDto
    ) throws Exception {
        // Forzamos el rol aquí en el controlador
        userDto.setRole(Role.PSYCHOLOGIST);
        AuthResponse response = authService.registerPsychologist(userDto, profileDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Autentica a un usuario existente y devuelve un JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest
    ) throws Exception {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }
}
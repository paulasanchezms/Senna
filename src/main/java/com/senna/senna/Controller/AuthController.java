package com.senna.senna.Controller;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.Service.AuthService;
import jakarta.validation.Valid;
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
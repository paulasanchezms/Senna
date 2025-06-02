package com.senna.senna.Controller;

import com.senna.senna.Config.JwtUtil;
import com.senna.senna.DTO.*;
import com.senna.senna.Entity.Role;
import com.senna.senna.Entity.User;
import com.senna.senna.Repository.UserRepository;
import com.senna.senna.Service.AuthService;
import com.senna.senna.Service.EmailService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    /**
     * Registra un nuevo usuario (paciente o psicólogo) y devuelve un JWT.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody CreateUserDTO dto
    ) throws Exception {
        AuthResponse response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra un psicólogo con su perfil profesional.
     */
    @PostMapping(
            value = "/register/psychologist",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AuthResponse> registerPsychologist(
            @Valid @RequestPart("user") CreateUserDTO userDto,
            @Valid @RequestPart("profile") CreatePsychologistProfileDTO profileDto
    ) throws Exception {
        userDto.setRole(Role.PSYCHOLOGIST);
        AuthResponse response = authService.registerPsychologist(userDto, profileDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Autentica a un usuario existente y devuelve un JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            AuthResponse response = authService.login(authRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Correo o contraseña incorrectos"));
        }
    }

    /**
     * Valida el token de recuperación de contraseña.
     */
    @PostMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestBody String token) {
        String email = jwtUtil.getClaim(token, Claims::getSubject);
        boolean isValid = jwtUtil.validateTokenByEmail(token, email);

        Map<String, String> response = new HashMap<>();
        if (isValid) {
            response.put("message", "Token válido");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Token inválido o expirado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Cambia la contraseña del usuario después de validar el token.
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        // Validar token y obtener email
        String email = jwtUtil.getClaim(token, Claims::getSubject);
        if (!jwtUtil.validateTokenByEmail(token, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token inválido o expirado"));
        }

        // Buscar el usuario
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        // Cambiar y guardar contraseña codificada
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }


    /**
     * Enviar correo de recuperación de contraseña si el email existe.
     */
    @PostMapping("/send-reset-password-email")
    public ResponseEntity<?> sendPasswordRecovery(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Buscar usuario por email
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "No existe ninguna cuenta con ese correo."));
        }

        // Generar token JWT
        String token = jwtUtil.generatePasswordResetToken(email);
        String urlRecuperarClave = new EmailDTO().getUrlRecuperarClave() + "?token=" + token;

        // Construir el email
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTitulo("Estimado/a " + user.getName() + " " + user.getLastName() + ",");
        emailDTO.setCuerpo("Has solicitado recuperar tu contraseña. Si no lo hiciste tú, ignora este mensaje. Si deseas continuar, haz clic en el siguiente botón:");
        emailDTO.setDestinatario(email);
        emailDTO.setMotivo("Recuperación de contraseña");
        emailDTO.setAsunto("Senna - Recuperación de contraseña");

        // Enviar correo
        emailService.enviarCorreo(emailDTO, urlRecuperarClave);

        return ResponseEntity.ok(Map.of("message", "Correo de recuperación enviado correctamente."));
    }
}
package com.senna.senna.Service;

import com.senna.senna.DTO.*;
import com.senna.senna.Config.JwtUtil;
import com.senna.senna.Config.CustomUserDetailsService;
import com.senna.senna.Entity.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserServiceImpl userServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PsychologistProfileServiceImpl psychologistProfileServiceImpl;

    public AuthServiceImpl(
            UserServiceImpl userServiceImpl,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            AuthenticationManager authenticationManager,
            PsychologistProfileServiceImpl psychologistProfileServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.psychologistProfileServiceImpl = psychologistProfileServiceImpl;
    }

    @Override
    public AuthResponse register(CreateUserDTO dto) throws Exception {
        // igual que antes, sin perfil
        if (userServiceImpl.emailExists(dto.getEmail())) {
            throw new Exception("El email ya está en uso");
        }
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        dto.setRole(Role.PATIENT);
        UserResponseDTO user = userServiceImpl.createUser(dto);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse registerPsychologist(
            CreateUserDTO dto,
            CreatePsychologistProfileDTO profileDto
    ) throws Exception {
        // 1) comprobación de email duplicado
        if (userServiceImpl.emailExists(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        // 2) validación mínima de datos del perfil
        if (profileDto.getConsultationDuration() == null) {
            throw new IllegalArgumentException("La duración de la consulta es obligatoria.");
        }
        if (profileDto.getConsultationPrice() == null) {
            throw new IllegalArgumentException("El precio de la consulta es obligatorio.");
        }
        if (isNullOrBlank(profileDto.getLocation())) {
            throw new IllegalArgumentException("La ubicación es obligatoria.");
        }
        if (isNullOrBlank(profileDto.getSpecialty())) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }

        // 3) codificar contraseña y preparar usuario
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        dto.setRole(Role.PSYCHOLOGIST);
        dto.setActive(false);

        // 4) crear usuario solo si todo lo anterior está correcto
        UserResponseDTO user = userServiceImpl.createUser(dto);

        // 5) crear perfil ligado al usuario recién creado
        psychologistProfileServiceImpl.createProfile(user.getId_user(), profileDto);

        // 6) generar JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(token);
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    /**
     * Autentica al usuario y genera un JWT.
     */
    @Override
    public AuthResponse login(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciales incorrectas", e);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        return new AuthResponse(jwt);
    }
}
package com.senna.senna.Service;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
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

    public AuthServiceImpl(
            UserServiceImpl userServiceImpl,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            AuthenticationManager authenticationManager
    ) {
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registra un nuevo usuario (paciente o psicólogo) y devuelve un JWT.
     */

    public AuthResponse register(CreateUserDTO dto) throws Exception {
        // 1) Verificar si el email ya está en uso
        if (userServiceImpl.emailExists(dto.getEmail())) {
            throw new Exception("El email ya está en uso");
        }

        // 2) Codificar la contraseña
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 3) Crear el usuario y devolver su DTO
        UserResponseDTO registeredUser = userServiceImpl.createUser(dto);

        // 4) Cargar UserDetails y generar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(registeredUser.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token);
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
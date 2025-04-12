package com.senna.senna.Service;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Config.JwtUtil;
import com.senna.senna.Config.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(CreateUserDTO dto) throws Exception {
        // Verificar si el email ya está en uso
        if(userService.emailExists(dto.getEmail())){
            throw new Exception("El email ya está en uso");
        }
        // Codificar la contraseña antes de guardar
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Crear el usuario y obtener el DTO de usuario registrado (sin datos sensibles)
        UserResponseDTO registeredUser = userService.createUser(dto);

        // Cargar los detalles del usuario para generar el token (usando, por ejemplo, el email)
        UserDetails userDetails = userDetailsService.loadUserByUsername(registeredUser.getEmail());

        // Generar el token JWT
        String token = jwtUtil.generateToken(userDetails);

        // Devolver solo el token en la respuesta
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciales incorrectas", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        return new AuthResponse(jwt);
    }
}
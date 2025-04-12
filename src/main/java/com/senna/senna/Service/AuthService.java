package com.senna.senna.Service;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreateUserDTO;

public interface AuthService {
    AuthResponse register(CreateUserDTO dto) throws Exception;
    AuthResponse login(AuthRequest authRequest) throws Exception;
}
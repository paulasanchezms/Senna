package com.senna.senna.Service;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreateUserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    AuthResponse register(CreateUserDTO dto, MultipartFile document) throws Exception;
    AuthResponse login(AuthRequest authRequest) throws Exception;
}
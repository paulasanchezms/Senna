package com.senna.senna.Service;

import com.senna.senna.DTO.AuthResponse;
import com.senna.senna.DTO.AuthRequest;
import com.senna.senna.DTO.CreatePsychologistProfileDTO;
import com.senna.senna.DTO.CreateUserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    AuthResponse register(CreateUserDTO dto) throws Exception;
    AuthResponse registerPsychologist(
            CreateUserDTO dto,
            CreatePsychologistProfileDTO profileDto
    ) throws Exception;
    AuthResponse login(AuthRequest authRequest) throws Exception;
}
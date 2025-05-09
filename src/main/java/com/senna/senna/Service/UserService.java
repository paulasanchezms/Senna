package com.senna.senna.Service;

import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO createUser(CreateUserDTO dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    Optional<UserResponseDTO> getUserByEmail(String email);
    List<UserResponseDTO> getAllPatients();
    void assignPatientToPsychologist(Long psychologistId, Long patientId);
    UserResponseDTO updateUser(Long id, UpdateUserDTO dto);
    void deleteUser(Long id);
    boolean emailExists(String email);
}
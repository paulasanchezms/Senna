package com.senna.senna.Service;

import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.Role;
import com.senna.senna.Entity.User;
import com.senna.senna.Mapper.UserMapper;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDTO createUser(CreateUserDTO dto) {
        User user = UserMapper.fromDTO(dto);
        User saved = userRepository.save(user);
        return UserMapper.toResponseDTO(saved);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> getAllPsychologists() {
        return userRepository.findByRole(Role.PSYCHOLOGIST)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
        return UserMapper.toResponseDTO(user);
    }

    @Override
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toResponseDTO);
    }

    @Override
    public List<UserResponseDTO> getAllPatients() {
        return userRepository.findByRole(Role.PATIENT)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void assignPatientToPsychologist(Long psychologistId, Long patientId) {
        User psy = userRepository.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + psychologistId));
        User pat = userRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado: " + patientId));
        if (psy.getRole() != Role.PSYCHOLOGIST || pat.getRole() != Role.PATIENT) {
            throw new IllegalArgumentException("Roles incorrectos para asignación");
        }
        psy.getPatients().add(pat);
        pat.getPsychologists().add(psy);
        userRepository.save(psy);
        userRepository.save(pat);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
        UserMapper.updateUserFromDTO(user, dto);
        User updated = userRepository.save(user);
        return UserMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
        userRepository.delete(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public List<UserResponseDTO> getPsychologistsBySpecialty(String specialty) {
        return userRepository.findByRoleAndProfileSpecialtyContainingIgnoreCase(Role.PSYCHOLOGIST, specialty).stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
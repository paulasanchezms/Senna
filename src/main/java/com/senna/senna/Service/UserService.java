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
public class UserService {

    private final UserRepository userRepository;

    // Crear usuario
    public UserResponseDTO createUser(CreateUserDTO dto) {
        User user = UserMapper.fromDTO(dto);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDTO(savedUser);
    }

    // Obtener todos los usuarios (como DTOs)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener usuario por ID (como DTO)
    public UserResponseDTO getUserById(Long id) {
        User user = getUserEntityById(id);
        return UserMapper.toResponseDTO(user);
    }

    // Obtener usuario por email (como DTO opcional)
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toResponseDTO);
    }

    // Obtener todos los psicólogos (como DTOs)
    public List<UserResponseDTO> getAllPsychologists() {
        return userRepository.findByRole(Role.PSYCHOLOGIST)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar psicólogos por especialidad (como DTOs)
    public List<UserResponseDTO> getPsychologistsBySpecialty(String specialty) {
        return userRepository.findByRoleAndSpecialtyContainingIgnoreCase(Role.PSYCHOLOGIST, specialty)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Obtener todos los pacientes (como DTOs)
    public List<UserResponseDTO> getAllPatients() {
        return userRepository.findByRole(Role.PATIENT)
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Asignar paciente a psicólogo (usando entidades reales)
    public void assignPatientToPsychologist(Long idPsychologist, Long idPatient) {
        User psychologist = getUserEntityById(idPsychologist);
        User patient = getUserEntityById(idPatient);

        if (psychologist.getRole() != Role.PSYCHOLOGIST || patient.getRole() != Role.PATIENT) {
            throw new IllegalArgumentException("Roles incorrectos para la asignación");
        }

        psychologist.getPatients().add(patient);
        patient.getPsychologists().add(psychologist);

        userRepository.save(psychologist);
        userRepository.save(patient);
    }

    // Actualizar usuario
    public UserResponseDTO updateUser(Long id, UpdateUserDTO dto) {
        User user = getUserEntityById(id);
        UserMapper.updateUserFromDTO(user, dto);
        return UserMapper.toResponseDTO(userRepository.save(user));
    }

    // Método privado para obtener entidad real
    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

    //Eliminar
    public void deleteUser(Long id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
    }

    //Ver si existe email
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + email));
    }

    public User findByIdEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

}
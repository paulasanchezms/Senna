package com.senna.senna.Service;

import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.Entity.Role;
import com.senna.senna.Entity.User;
import com.senna.senna.Mapper.UserMapper;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Crear usuario
    public User createUser(User user) {
        // Puedes agregar validaciones según el rol aquí
        return userRepository.save(user);
    }

    // Obtener todos
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener por ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
    }

    // Obtener por email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Obtener todos los psicólogos
    public List<User> getAllPsychologists() {
        return userRepository.findByRole(Role.PSYCHOLOGIST);
    }

    // Obtener psicólogos por especialidad
    public List<User> getPsychologistsBySpecialty(String specialty) {
        return userRepository.findByRoleAndSpecialtyContainingIgnoreCase(Role.PSYCHOLOGIST, specialty);
    }

    // Obtener todos los pacientes
    public List<User> getAllPatients() {
        return userRepository.findByRole(Role.PATIENT);
    }

    // Asignar relación paciente-psicólogo
    public void assignPatientToPsychologist(Long idPsychologist, Long idPatient) {
        User psychologist = getUserById(idPsychologist);
        User patient = getUserById(idPatient);

        if (psychologist.getRole() != Role.PSYCHOLOGIST || patient.getRole() != Role.PATIENT) {
            throw new IllegalArgumentException("Roles incorrectos para la asignación");
        }

        psychologist.getPatients().add(patient);
        patient.getPsychologists().add(psychologist);

        userRepository.save(psychologist);
        userRepository.save(patient);
    }


    public User updateUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserMapper.updateUserFromDTO(user, dto);
        return userRepository.save(user);
    }
}
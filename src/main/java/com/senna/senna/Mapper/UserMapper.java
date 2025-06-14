package com.senna.senna.Mapper;

import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.PsychologistProfileDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.User;

public class UserMapper {

    // Convierte un CreateUserDTO en una entidad User
    public static User fromDTO(CreateUserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setLastName(dto.getLast_name());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setActive(dto.isActive());
        user.setTermsAccepted(dto.isTermsAccepted());
        return user;
    }

    // Convierte una entidad User a un UserResponseDTO (para respuestas)
    public static UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId_user(user.getId());
        dto.setName(user.getName());
        dto.setLast_name(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPhone(user.getPhone());
        dto.setPhotoUrl(user.getPhotoUrl());
        dto.setActive(user.isActive());
        dto.setTermsAccepted(user.isTermsAccepted());

        // Si el usuario tiene perfil profesional, se incluye en el DTO
        if (user.getProfile() != null) {
            PsychologistProfileDTO profileDTO = new PsychologistProfileDTO();
            profileDTO.setConsultationDuration(user.getProfile().getConsultationDuration());
            profileDTO.setConsultationPrice(user.getProfile().getConsultationPrice());
            profileDTO.setSpecialty(user.getProfile().getSpecialty());
            profileDTO.setLocation(user.getProfile().getLocation());
            profileDTO.setDocument(user.getProfile().getDocument());
            profileDTO.setDescription(user.getProfile().getDescription());
            dto.setProfile(profileDTO);
        }
        return dto;
    }

    // Aplica los cambios de un UpdateUserDTO sobre un usuario existente
    public static void updateUserFromDTO(User user, UpdateUserDTO dto) {
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getLast_name() != null) {
            user.setLastName(dto.getLast_name());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getPhotoUrl() != null) {
            user.setPhotoUrl(dto.getPhotoUrl());
        }
    }
}
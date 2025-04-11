package com.senna.senna.Mapper;

import com.senna.senna.DTO.CreateUserDTO;
import com.senna.senna.DTO.UpdateUserDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.User;

public class UserMapper {

    public static User fromDTO(CreateUserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setLast_name(dto.getLast_name());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setDni(dto.getDni());
        user.setQualification(dto.getQualification());
        user.setSpecialty(dto.getSpecialty());
        user.setLocation(dto.getLocation());
        user.setDocument(dto.getDocument());
        return user;
    }

    public static UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId_user(user.getId_user());
        dto.setName(user.getName());
        dto.setLast_name(user.getLast_name());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDni(user.getDni());
        dto.setQualification(user.getQualification());
        dto.setSpecialty(user.getSpecialty());
        dto.setLocation(user.getLocation());
        dto.setDocument(user.getDocument());
        return dto;
    }

    public static void updateUserFromDTO(User user, UpdateUserDTO dto) {
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getLast_name() != null) user.setLast_name(dto.getLast_name());
        if (dto.getQualification() != null) user.setQualification(dto.getQualification());
        if (dto.getSpecialty() != null) user.setSpecialty(dto.getSpecialty());
        if (dto.getLocation() != null) user.setLocation(dto.getLocation());
        if (dto.getDocument() != null) user.setDocument(dto.getDocument());
    }
}


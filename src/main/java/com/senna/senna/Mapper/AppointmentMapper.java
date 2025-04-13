package com.senna.senna.Mapper;

import com.senna.senna.DTO.AppointmentResponseDTO;
import com.senna.senna.Entity.Appointment;

public class AppointmentMapper {
    public static AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setDateTime(appointment.getDateTime());
        dto.setDuration(appointment.getDuration());
        dto.setStatus(appointment.getStatus());
        dto.setDescription(appointment.getDescription());
        // Convertir la entidad usuario a un DTO sin campos sensibles
        dto.setPatient(UserMapper.toResponseDTO(appointment.getPatient()));
        dto.setPsychologist(UserMapper.toResponseDTO(appointment.getPsychologist()));
        return dto;
    }
}
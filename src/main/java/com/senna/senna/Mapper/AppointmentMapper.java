package com.senna.senna.Mapper;

import com.senna.senna.DTO.AppointmentResponseDTO;
import com.senna.senna.DTO.CreateAppointmentDTO;
import com.senna.senna.Entity.Appointment;
import com.senna.senna.Entity.User;



/**
 * Mapper para convertir entre la entidad Appointment y sus DTOs.
 */
public class AppointmentMapper {

    /**
     * Mapea una entidad Appointment a un DTO de respuesta.
     */
    public static AppointmentResponseDTO toDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setPatient(UserMapper.toResponseDTO(appointment.getPatient()));
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPsychologistId(appointment.getPsychologist().getId());
        dto.setPsychologist(UserMapper.toResponseDTO(appointment.getPsychologist()));
        dto.setDateTime(appointment.getDateTime());
        dto.setDuration(appointment.getDuration());
        dto.setStatus(appointment.getStatus().name());
        dto.setDescription(appointment.getDescription());
        return dto;
    }

    /**
     * Mapea un DTO de creación a la entidad Appointment.
     */
    public static Appointment toEntity(CreateAppointmentDTO dto, User patient, User psychologist) {
        Appointment entity = new Appointment();
        entity.setPatient(patient);
        entity.setPsychologist(psychologist);
        entity.setDateTime(dto.getDateTime());
        entity.setDuration(dto.getDuration());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
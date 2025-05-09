package com.senna.senna.Mapper;

import com.senna.senna.DTO.PsychologistProfileDTO;
import com.senna.senna.DTO.CreatePsychologistProfileDTO;
import com.senna.senna.DTO.UpdatePsychologistProfileDTO;
import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Entity.PsychologistProfile;
import com.senna.senna.Entity.WorkingHour;
import com.senna.senna.Entity.User;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;

public class PsychologistProfileMapper {

    /** Mapea DTO de creación a entidad, asociándolo a un User existente */
    public static PsychologistProfile toEntity(CreatePsychologistProfileDTO dto, User user) {
        PsychologistProfile profile = new PsychologistProfile();
        profile.setUser(user);
        profile.setConsultationDuration(dto.getConsultationDuration());
        profile.setConsultationPrice(dto.getConsultationPrice());
        profile.setSpecialty(dto.getSpecialty());
        profile.setLocation(dto.getLocation());

        List<WorkingHour> hours = dto.getWorkingHours().stream()
                .map(whDto -> {
                    WorkingHour wh = new WorkingHour();
                    wh.setDayOfWeek(whDto.getDayOfWeek());
                    wh.setStartTime(LocalTime.parse(whDto.getStartTime()));
                    wh.setEndTime(LocalTime.parse(whDto.getEndTime()));
                    wh.setProfile(profile);
                    return wh;
                })
                .collect(Collectors.toList());
        profile.setWorkingHours(hours);

        return profile;
    }

    /** Mapea la entidad a DTO de respuesta */
    public static PsychologistProfileDTO toDTO(PsychologistProfile profile) {
        PsychologistProfileDTO dto = new PsychologistProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setConsultationDuration(profile.getConsultationDuration());
        dto.setConsultationPrice(profile.getConsultationPrice());
        dto.setSpecialty(profile.getSpecialty());
        dto.setLocation(profile.getLocation());

        List<WorkingHourDTO> hoursDto = profile.getWorkingHours().stream()
                .map(wh -> {
                    WorkingHourDTO whDto = new WorkingHourDTO();
                    whDto.setDayOfWeek(wh.getDayOfWeek());
                    whDto.setStartTime(wh.getStartTime().toString());
                    whDto.setEndTime(wh.getEndTime().toString());
                    return whDto;
                })
                .collect(Collectors.toList());
        dto.setWorkingHours(hoursDto);

        return dto;
    }

    /** Actualiza la entidad existente con campos del DTO de actualización */
    public static void updateEntity(PsychologistProfile profile, UpdatePsychologistProfileDTO dto) {
        if (dto.getConsultationDuration() != null) {
            profile.setConsultationDuration(dto.getConsultationDuration());
        }
        if (dto.getConsultationPrice() != null) {
            profile.setConsultationPrice(dto.getConsultationPrice());
        }
        if (dto.getSpecialty() != null) {
            profile.setSpecialty(dto.getSpecialty());
        }
        if (dto.getLocation() != null) {
            profile.setLocation(dto.getLocation());
        }
    }
}
package com.senna.senna.Mapper;

import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Entity.WorkingHour;
import java.time.LocalTime;

/**
 * Mapper para convertir entre la entidad WorkingHour y su DTO.
 */
public class WorkingHourMapper {

    /**
     * Convierte una entidad WorkingHour en un DTO.
     * @param wh entidad a convertir
     * @return DTO con los datos básicos de la franja horaria
     */
    public static WorkingHourDTO toDTO(WorkingHour wh) {
        WorkingHourDTO dto = new WorkingHourDTO();
        dto.setDayOfWeek(wh.getDayOfWeek());
        dto.setStartTime(wh.getStartTime().toString());
        dto.setEndTime(wh.getEndTime().toString());
        return dto;
    }

    /**
     * Convierte un DTO en una entidad WorkingHour.
     * El campo `profile` debe ser asignado manualmente desde el servicio.
     * @param dto DTO con los datos de horario
     * @return entidad WorkingHour sin perfil asignado aún
     */
    public static WorkingHour toEntity(WorkingHourDTO dto) {
        WorkingHour wh = new WorkingHour();
        wh.setDayOfWeek(dto.getDayOfWeek());
        wh.setStartTime(LocalTime.parse(dto.getStartTime()));
        wh.setEndTime(LocalTime.parse(dto.getEndTime()));
        return wh;
    }
}

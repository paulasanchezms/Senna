package com.senna.senna.Mapper;

import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Entity.WorkingHour;
import java.time.LocalTime;

/**
 * Mapper para convertir entre la entidad WorkingHour y su DTO.
 */
public class WorkingHourMapper {

    /**
     * Mapea una entidad WorkingHour a su DTO.
     * @param wh entidad a convertir
     * @return DTO con datos de la franja horaria
     */
    public static WorkingHourDTO toDTO(WorkingHour wh) {
        WorkingHourDTO dto = new WorkingHourDTO();
        dto.setDayOfWeek(wh.getDayOfWeek());
        dto.setStartTime(wh.getStartTime().toString());
        dto.setEndTime(wh.getEndTime().toString());
        return dto;
    }

    /**
     * Mapea un DTO a una nueva entidad WorkingHour.
     * El profile debe asignarse externamente.
     * @param dto datos de la franja horaria
     * @return entidad sin profile aún
     */
    public static WorkingHour toEntity(WorkingHourDTO dto) {
        WorkingHour wh = new WorkingHour();
        wh.setDayOfWeek(dto.getDayOfWeek());
        wh.setStartTime(LocalTime.parse(dto.getStartTime()));
        wh.setEndTime(LocalTime.parse(dto.getEndTime()));
        return wh;
    }
}

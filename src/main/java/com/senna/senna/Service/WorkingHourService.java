package com.senna.senna.Service;

import com.senna.senna.DTO.WorkingHourDTO;
import java.util.List;

/**
 * Servicio para CRUD de franjas horarias de psicólogo.
 */
public interface WorkingHourService {
    List<WorkingHourDTO> getWorkingHours(Long userId);
    WorkingHourDTO createWorkingHour(Long userId, WorkingHourDTO dto);
    WorkingHourDTO updateWorkingHour(Long userId, Long hourId, WorkingHourDTO dto);
    void deleteWorkingHour(Long userId, Long hourId);
    List<WorkingHourDTO> replaceWorkingHours(Long userId, List<WorkingHourDTO> hoursDto);
}
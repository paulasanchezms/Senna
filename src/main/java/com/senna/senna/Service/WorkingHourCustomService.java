package com.senna.senna.Service;

import com.senna.senna.DTO.WorkingHourCustomDTO;

import java.util.List;

public interface WorkingHourCustomService {
    List<WorkingHourCustomDTO> getByDate(Long profileId, String dateStr);
    void replaceByDate(Long profileId, String dateStr, List<WorkingHourCustomDTO> dtos);
}

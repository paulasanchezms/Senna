package com.senna.senna.Service;

import com.senna.senna.DTO.CreatePsychologistProfileDTO;
import com.senna.senna.DTO.PsychologistProfileDTO;
import com.senna.senna.DTO.UpdatePsychologistProfileDTO;
import com.senna.senna.DTO.WorkingHourDTO;
import java.util.List;

public interface PsychologistProfileService {
    PsychologistProfileDTO getProfile(Long userId);
    PsychologistProfileDTO createProfile(Long userId, CreatePsychologistProfileDTO dto);
    PsychologistProfileDTO updateProfile(Long userId, UpdatePsychologistProfileDTO dto);
    void updateWorkingHours(Long userId, List<WorkingHourDTO> hours);

}

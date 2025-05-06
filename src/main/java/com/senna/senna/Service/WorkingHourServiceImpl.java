package com.senna.senna.Service;

import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Entity.PsychologistProfile;
import com.senna.senna.Entity.User;
import com.senna.senna.Entity.WorkingHour;
import com.senna.senna.Mapper.WorkingHourMapper;
import com.senna.senna.Repository.PsychologistProfileRepository;
import com.senna.senna.Repository.UserRepository;
import com.senna.senna.Repository.WorkingHourRepository;
import com.senna.senna.Service.WorkingHourService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
@Transactional
public class WorkingHourServiceImpl implements WorkingHourService {
    private final WorkingHourRepository hourRepo;
    private final PsychologistProfileRepository profileRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public List<WorkingHourDTO> getWorkingHours(Long userId) {
        return hourRepo.findByProfileUserId(userId).stream()
                .map(wh -> WorkingHourMapper.toDTO(wh))
                .collect(Collectors.toList());
    }

    @Override
    public WorkingHourDTO createWorkingHour(Long userId, WorkingHourDTO dto) {
        User psychologist = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));

        PsychologistProfile profile = profileRepo.findByUserId(psychologist.getId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado para userId: " + userId));

        WorkingHour wh = new WorkingHour();
        wh.setProfile(profile);
        wh.setDayOfWeek(dto.getDayOfWeek());
        wh.setStartTime(LocalTime.parse(dto.getStartTime()));
        wh.setEndTime(LocalTime.parse(dto.getEndTime()));

        WorkingHour saved = hourRepo.save(wh);
        return WorkingHourMapper.toDTO(saved);
    }

    @Override
    public WorkingHourDTO updateWorkingHour(Long userId, Long hourId, WorkingHourDTO dto) {
        WorkingHour wh = hourRepo.findById(hourId)
                .orElseThrow(() -> new EntityNotFoundException("Franja no encontrada: " + hourId));

        if (!wh.getProfile().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("No autorizado a modificar esta franja");
        }
        if (dto.getDayOfWeek() != null) wh.setDayOfWeek(dto.getDayOfWeek());
        if (dto.getStartTime() != null) wh.setStartTime(LocalTime.parse(dto.getStartTime()));
        if (dto.getEndTime() != null) wh.setEndTime(LocalTime.parse(dto.getEndTime()));

        WorkingHour updated = hourRepo.save(wh);
        return WorkingHourMapper.toDTO(updated);
    }

    @Override
    public void deleteWorkingHour(Long userId, Long hourId) {
        WorkingHour wh = hourRepo.findById(hourId)
                .orElseThrow(() -> new EntityNotFoundException("Franja no encontrada: " + hourId));
        if (!wh.getProfile().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("No autorizado a eliminar esta franja");
        }
        hourRepo.delete(wh);
    }
}

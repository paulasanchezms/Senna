package com.senna.senna.Service;

import com.senna.senna.DTO.WorkingHourCustomDTO;
import com.senna.senna.Entity.PsychologistProfile;
import com.senna.senna.Entity.WorkingHourCustom;
import com.senna.senna.Repository.PsychologistProfileRepository;
import com.senna.senna.Repository.WorkingHourCustomRepository;
import com.senna.senna.Service.WorkingHourCustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkingHourCustomServiceImpl implements WorkingHourCustomService {

    private final WorkingHourCustomRepository customRepo;
    private final PsychologistProfileRepository profileRepo;

    @Override
    public List<WorkingHourCustomDTO> getByDate(Long profileId, String dateStr) {
        PsychologistProfile profile = profileRepo.findByUserId(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado para el usuario con ID: " + profileId));
        LocalDate date = LocalDate.parse(dateStr);
        return customRepo.findByProfileAndDate(profile, date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void replaceByDate(Long profileId, String dateStr, List<WorkingHourCustomDTO> dtos) {
        PsychologistProfile profile = profileRepo.findByUserId(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado para el usuario con ID: " + profileId));
        LocalDate date = LocalDate.parse(dateStr);

        customRepo.deleteByProfileAndDate(profile, date);

        List<WorkingHourCustom> entities = dtos.stream()
                .map(dto -> WorkingHourCustom.builder()
                        .profile(profile)
                        .date(date)
                        .startTime(LocalTime.parse(dto.getStartTime()))
                        .endTime(LocalTime.parse(dto.getEndTime()))
                        .build())
                .collect(Collectors.toList());

        customRepo.saveAll(entities);
    }

    private WorkingHourCustomDTO mapToDTO(WorkingHourCustom wh) {
        WorkingHourCustomDTO dto = new WorkingHourCustomDTO();
        dto.setId(wh.getId());
        dto.setDate(wh.getDate().toString());
        dto.setStartTime(wh.getStartTime().toString());
        dto.setEndTime(wh.getEndTime().toString());
        return dto;
    }

}
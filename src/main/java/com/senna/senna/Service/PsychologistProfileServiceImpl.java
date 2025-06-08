package com.senna.senna.Service;

import com.senna.senna.DTO.CreatePsychologistProfileDTO;
import com.senna.senna.DTO.PsychologistProfileDTO;
import com.senna.senna.DTO.UpdatePsychologistProfileDTO;
import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Entity.PsychologistProfile;
import com.senna.senna.Entity.Role;
import com.senna.senna.Entity.User;
import com.senna.senna.Entity.WorkingHour;
import com.senna.senna.Mapper.PsychologistProfileMapper;
import com.senna.senna.Repository.PsychologistProfileRepository;
import com.senna.senna.Repository.UserRepository;
import com.senna.senna.Service.PsychologistProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class PsychologistProfileServiceImpl implements PsychologistProfileService {
    private final PsychologistProfileRepository profileRepo;
    private final UserRepository userRepo;

    /**
     * Obtiene el perfil de psicólogo asociado a un usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public PsychologistProfileDTO getProfile(Long userId) {
        PsychologistProfile profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado: " + userId));
        return PsychologistProfileMapper.toDTO(profile);
    }

    /**
     * Crea un nuevo perfil de psicólogo para un usuario con rol PSYCHOLOGIST.
     */
    @Override
    @Transactional
    public PsychologistProfileDTO createProfile(Long userId, CreatePsychologistProfileDTO dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + userId));
        if (user.getRole() != Role.PSYCHOLOGIST) {
            throw new IllegalArgumentException("Usuario sin rol PSYCHOLOGIST");
        }
        PsychologistProfile entity = PsychologistProfileMapper.toEntity(dto, user);
        PsychologistProfile saved = profileRepo.save(entity);
        return PsychologistProfileMapper.toDTO(saved);
    }


    /**
     * Actualiza los datos del perfil profesional del psicólogo.
     */
    @Override
    @Transactional
    public PsychologistProfileDTO updateProfile(Long userId, UpdatePsychologistProfileDTO dto) {
        PsychologistProfile profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado: " + userId));
        PsychologistProfileMapper.updateEntity(profile, dto);
        PsychologistProfile saved = profileRepo.save(profile);
        return PsychologistProfileMapper.toDTO(saved);
    }

    /**
     * Reemplaza completamente las franjas horarias del psicólogo por las nuevas proporcionadas.
     */
    @Override
    @Transactional
    public void updateWorkingHours(Long userId, List<WorkingHourDTO> hoursDto) {
        PsychologistProfile profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado: " + userId));
        profile.getWorkingHours().clear();
        List<WorkingHour> newHours = hoursDto.stream().map(whDto -> {
            WorkingHour wh = new WorkingHour();
            wh.setDayOfWeek(whDto.getDayOfWeek());
            wh.setStartTime(LocalTime.parse(whDto.getStartTime()));
            wh.setEndTime(LocalTime.parse(whDto.getEndTime()));
            wh.setProfile(profile);
            return wh;
        }).collect(Collectors.toList());
        profile.getWorkingHours().addAll(newHours);
        profileRepo.save(profile);
    }


}

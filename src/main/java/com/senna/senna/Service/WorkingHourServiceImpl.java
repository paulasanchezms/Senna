package com.senna.senna.Service;

import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Entity.PsychologistProfile;
import com.senna.senna.Entity.User;
import com.senna.senna.Entity.WorkingHour;
import com.senna.senna.Mapper.WorkingHourMapper;
import com.senna.senna.Repository.AppointmentRepository;
import com.senna.senna.Repository.PsychologistProfileRepository;
import com.senna.senna.Repository.UserRepository;
import com.senna.senna.Repository.WorkingHourRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
@Transactional
public class WorkingHourServiceImpl implements WorkingHourService {
    private final WorkingHourRepository hourRepo;
    private final PsychologistProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final AppointmentRepository appointmentRepo;

    /**
     * Devuelve todas las franjas horarias del psicólogo.
     */
    @Override
    @Transactional(readOnly = true)
    public List<WorkingHourDTO> getWorkingHours(Long userId) {
        return hourRepo.findByProfileUserId(userId).stream()
                .map(wh -> WorkingHourMapper.toDTO(wh))
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva franja horaria asociada al perfil del psicólogo.
     */
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


    /**
     * Actualiza una franja horaria existente, validando que pertenezca al usuario.
     */
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

    /**
     * Elimina una franja horaria del psicólogo si le pertenece.
     */
    @Override
    public void deleteWorkingHour(Long userId, Long hourId) {
        WorkingHour wh = hourRepo.findById(hourId)
                .orElseThrow(() -> new EntityNotFoundException("Franja no encontrada: " + hourId));
        if (!wh.getProfile().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("No autorizado a eliminar esta franja");
        }
        hourRepo.delete(wh);
    }

    /**
     * Reemplaza todas las franjas horarias del psicólogo con una nueva lista.
     */
    @Override
    public List<WorkingHourDTO> replaceWorkingHours(Long userId, List<WorkingHourDTO> hoursDto) {

        hourRepo.deleteByProfileUserId(userId);

        // 1) Obtengo perfil
        PsychologistProfile profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado: " + userId));
        // 2) Limpio las existentes
        profile.getWorkingHours().clear();
        // 3) Construyo y agrego las nuevas
        List<WorkingHour> nuevos = hoursDto.stream().map(dto -> {
            WorkingHour wh = new WorkingHour();
            wh.setProfile(profile);
            wh.setDayOfWeek(dto.getDayOfWeek());
            wh.setStartTime(LocalTime.parse(dto.getStartTime()));
            wh.setEndTime(LocalTime.parse(dto.getEndTime()));
            return wh;
        }).collect(Collectors.toList());
        profile.getWorkingHours().addAll(nuevos);
        // 4) Salvo perfil
        PsychologistProfile saved = profileRepo.save(profile);
        // 5) Devuelvo DTOs
        return saved.getWorkingHours().stream()
                .map(WorkingHourMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcula y devuelve todos los bloques de tiempo disponibles para un psicólogo en una fecha.
     * Se basa en sus franjas horarias y en las citas ya reservadas.
     */
    @Transactional(readOnly = true)
    @Override
    public List<String> getAvailableSlots(Long userId, LocalDate date) {
        // Obtener perfil
        PsychologistProfile profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado: " + userId));

        int duration = profile.getConsultationDuration();
        int dayOfWeek = date.getDayOfWeek().getValue() - 1;

        // Obtener franjas de ese día
        List<WorkingHour> hours = hourRepo.findByProfileUserIdAndDayOfWeek(userId, dayOfWeek);

        // Obtener citas ya reservadas de ese día
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<LocalTime> reservedTimes = appointmentRepo
                .findByPsychologistIdAndDateTimeBetween(userId, startOfDay, endOfDay)
                .stream()
                .map(appt -> appt.getDateTime().toLocalTime())
                .collect(Collectors.toList());

        // Generar posibles bloques
        List<String> slots = new ArrayList<>();
        for (WorkingHour wh : hours) {
            LocalTime start = wh.getStartTime();
            LocalTime end = wh.getEndTime();

            while (!start.plusMinutes(duration).isAfter(end)) {
                if (!reservedTimes.contains(start)) {
                    slots.add(start.toString().substring(0,5));
                }
                start = start.plusMinutes(duration);
            }
        }

        return slots;
    }
}

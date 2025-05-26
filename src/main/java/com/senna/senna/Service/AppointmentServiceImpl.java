package com.senna.senna.Service;

import com.senna.senna.DTO.AppointmentResponseDTO;
import com.senna.senna.DTO.CreateAppointmentDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.*;
import com.senna.senna.Mapper.AppointmentMapper;
import com.senna.senna.Mapper.UserMapper;
import com.senna.senna.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PsychologistProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final WorkingHourCustomRepository customHourRepo;

    @Override
    @Transactional
    public AppointmentResponseDTO scheduleAppointment(CreateAppointmentDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        if (dto.getDateTime().isBefore(now.plusHours(1))) {
            throw new IllegalArgumentException("No se puede reservar una cita antes de 1 hora desde ahora.");
        }
        User patient = userRepo.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado: " + dto.getPatientId()));
        User psychologist = userRepo.findById(dto.getPsychologistId())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + dto.getPsychologistId()));

        boolean exists = appointmentRepo.existsByPsychologistAndPatientAndDateTimeAndStatusIn(
                psychologist,
                patient,
                dto.getDateTime(),
                List.of(AppointmentStatus.PENDIENTE, AppointmentStatus.CONFIRMADA)
        );
        if (exists) {
            throw new IllegalStateException("Ya existe una cita para esa fecha y hora.");
        }

        Appointment entity = AppointmentMapper.toEntity(dto, patient, psychologist);
        entity.setStatus(AppointmentStatus.PENDIENTE);
        Appointment saved = appointmentRepo.save(entity);
        return AppointmentMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableTimes(Long psychologistId, LocalDate date) {
        User psychologist = userRepo.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + psychologistId));

        PsychologistProfile profile = profileRepo.findByUserIdWithWorkingHours(psychologist.getId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado para userId: " + psychologistId));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<LocalTime> booked = appointmentRepo
                .findByPsychologistAndDateTimeBetween(psychologist, startOfDay, endOfDay)
                .stream()
                .filter(ap -> ap.getStatus() == AppointmentStatus.PENDIENTE || ap.getStatus() == AppointmentStatus.CONFIRMADA)
                .map(ap -> ap.getDateTime().toLocalTime())
                .collect(Collectors.toList());

        int duration = profile.getConsultationDuration();
        if (duration <= 0) {
            throw new IllegalArgumentException("Duración de consulta inválida: " + duration);
        }

        int dow = (date.getDayOfWeek().getValue() + 6) % 7;
        List<WorkingHour> hoursForDay = getEffectiveWorkingHours(profile, date, dow);

        return calculateSlots(hoursForDay, booked, duration);
    }



    @Transactional(readOnly = true)
    public Map<LocalDate, List<String>> getAvailableTimesForWeek(Long psychologistId, LocalDate startDate, LocalDate endDate) {
        User psychologist = userRepo.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + psychologistId));

        PsychologistProfile profile = profileRepo.findByUserIdWithWorkingHours(psychologist.getId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado para userId: " + psychologistId));

        List<Appointment> appointments = appointmentRepo.findByPsychologistAndDateTimeBetween(
                        psychologist, startDate.atStartOfDay(), endDate.atTime(23, 59))
                .stream()
                .filter(ap -> ap.getStatus() == AppointmentStatus.PENDIENTE || ap.getStatus() == AppointmentStatus.CONFIRMADA)
                .collect(Collectors.toList());

        Map<LocalDate, List<LocalTime>> bookedByDay = appointments.stream()
                .collect(Collectors.groupingBy(
                        ap -> ap.getDateTime().toLocalDate(),
                        Collectors.mapping(ap -> ap.getDateTime().toLocalTime(), Collectors.toList())
                ));

        int duration = profile.getConsultationDuration();
        if (duration <= 0) {
            throw new IllegalArgumentException("Duración de consulta inválida: " + duration);
        }

        Map<LocalDate, List<String>> available = new HashMap<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int dow = (date.getDayOfWeek().getValue() + 6) % 7;            List<LocalTime> booked = bookedByDay.getOrDefault(date, Collections.emptyList());
            List<WorkingHour> hoursForDay = getEffectiveWorkingHours(profile, date, dow);
            List<String> slots = calculateSlots(hoursForDay, booked, duration);
            available.put(date, slots);
        }

        return available;
    }

    private List<WorkingHour> getEffectiveWorkingHours(PsychologistProfile profile, LocalDate date, int dow) {
        List<WorkingHourCustom> customHours = customHourRepo.findByProfileAndDate(profile, date);
        if (!customHours.isEmpty()) {
            return customHours.stream()
                    .map(c -> WorkingHour.builder()
                            .profile(profile)
                            .dayOfWeek(dow)
                            .startTime(c.getStartTime())
                            .endTime(c.getEndTime())
                            .build())
                    .collect(Collectors.toList());
        }
        return profile.getWorkingHours().stream()
                .filter(wh -> wh.getDayOfWeek() == dow)
                .collect(Collectors.toList());
    }

    private List<String> calculateSlots(List<WorkingHour> hours, List<LocalTime> booked, int duration) {
        List<String> slots = new ArrayList<>();

        for (WorkingHour wh : hours) {
            LocalTime start = wh.getStartTime();
            LocalTime end = wh.getEndTime().minusMinutes(duration);

            for (LocalTime time = start; !time.isAfter(end); time = time.plusMinutes(duration)) {
                final LocalTime currentTime = time;
                boolean isBooked = booked.stream()
                        .anyMatch(b -> b.truncatedTo(ChronoUnit.MINUTES).equals(currentTime));

                if (!isBooked) {
                    slots.add(time.toString());
                }
            }
        }

        Collections.sort(slots);
        return slots;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateAppointment(Long appointmentId, CreateAppointmentDTO dto) {
        Appointment ap = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada: " + appointmentId));
        ap.setDateTime(dto.getDateTime());
        ap.setDuration(dto.getDuration());
        ap.setDescription(dto.getDescription());
        Appointment updated = appointmentRepo.save(ap);
        return AppointmentMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment ap = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada: " + appointmentId));
        ap.setStatus(AppointmentStatus.CANCELADA);
        appointmentRepo.save(ap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsForPatient(Long patientId) {
        User patient = userRepo.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado: " + patientId));
        return appointmentRepo.findByPatient(patient)
                .stream()
                .map(AppointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsForPsychologist(Long psychologistId) {
        User psychologist = userRepo.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + psychologistId));
        return appointmentRepo.findByPsychologist(psychologist)
                .stream()
                .map(AppointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getPendingAppointmentsForPsychologist(Long psychologistId) {
        User psychologist = userRepo.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + psychologistId));
        return appointmentRepo.findByPsychologistAndStatus(psychologist, AppointmentStatus.PENDIENTE)
                .stream()
                .map(AppointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptAppointment(Long appointmentId) {
        Appointment ap = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada: " + appointmentId));

        ap.setStatus(AppointmentStatus.CONFIRMADA);
        appointmentRepo.save(ap);

        User psychologist = ap.getPsychologist();
        User patient = ap.getPatient();

        // Añade si aún no está asignado
        if (!psychologist.getPatients().contains(patient)) {
            psychologist.getPatients().add(patient);
            patient.getPsychologists().add(psychologist);
            userRepo.save(psychologist);
            userRepo.save(patient);
        }
    }

    @Override
    @Transactional
    public void rejectAppointment(Long appointmentId) {
        Appointment ap = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada: " + appointmentId));
        ap.setStatus(AppointmentStatus.CANCELADA);
        appointmentRepo.save(ap);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getPatientsForPsychologist(Long psychologistId) {
        User psychologist = userRepo.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado"));
        return appointmentRepo.findByPsychologist(psychologist).stream()
                .map(Appointment::getPatient)
                .distinct()
                .map(UserMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void cancelAllAppointmentsWithPsychologist(Long patientId, Long psychologistId) {
        List<Appointment> appointments = appointmentRepo.findByPatient_IdAndPsychologist_IdAndStatusIn(
                patientId, psychologistId, List.of(AppointmentStatus.CONFIRMADA, AppointmentStatus.PENDIENTE)
        );

        for (Appointment appt : appointments) {
            appt.setStatus(AppointmentStatus.CANCELADA);
        }

        appointmentRepo.saveAll(appointments);
    }


}
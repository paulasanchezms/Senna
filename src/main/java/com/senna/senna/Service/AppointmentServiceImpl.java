package com.senna.senna.Service;

import com.senna.senna.DTO.AppointmentResponseDTO;
import com.senna.senna.DTO.CreateAppointmentDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.Appointment;
import com.senna.senna.Entity.AppointmentStatus;
import com.senna.senna.Entity.PsychologistProfile;
import com.senna.senna.Entity.User;
import com.senna.senna.Mapper.AppointmentMapper;
import com.senna.senna.Mapper.UserMapper;
import com.senna.senna.Repository.AppointmentRepository;
import com.senna.senna.Repository.PsychologistProfileRepository;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PsychologistProfileRepository profileRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public AppointmentResponseDTO scheduleAppointment(CreateAppointmentDTO dto) {
        User patient = userRepo.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado: " + dto.getPatientId()));
        User psychologist = userRepo.findById(dto.getPsychologistId())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + dto.getPsychologistId()));

        boolean exists = appointmentRepo.existsByPsychologistAndPatientAndDateTimeAndStatusIn(
                psychologist,
                patient,
                dto.getDateTime(),
                List.of(AppointmentStatus.PENDIENTE, AppointmentStatus.CONFIRMADA)
        );       if (exists) {
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
                .map(ap -> ap.getDateTime().toLocalTime())
                .collect(Collectors.toList());

        int duration = profile.getConsultationDuration();
        if (duration <= 0) {
            throw new IllegalArgumentException("Duración de consulta inválida: " + duration);
        }

        int dow = date.getDayOfWeek().getValue() % 7;

        return profile.getWorkingHours().stream()
                .filter(wh -> wh.getDayOfWeek() == dow)
                .flatMap(wh -> {
                    if (wh.getStartTime().isAfter(wh.getEndTime()) || wh.getStartTime().equals(wh.getEndTime())) {
                        System.out.println("Horario inválido: " + wh);
                        return Stream.empty();
                    }

                    LocalTime slot = wh.getStartTime();
                    LocalTime limit = wh.getEndTime().minusMinutes(duration);
                    List<String> slots = new ArrayList<>();

                    while (!slot.isAfter(limit)) {
                        if (!booked.contains(slot)) {
                            slots.add(slot.toString());
                        }
                        slot = slot.plusMinutes(duration);
                    }
                    return slots.stream();
                })
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, List<String>> getAvailableTimesForWeek(Long psychologistId, LocalDate startDate, LocalDate endDate) {
        User psychologist = userRepo.findById(psychologistId)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + psychologistId));
        PsychologistProfile profile = profileRepo.findByUserIdWithWorkingHours(psychologist.getId())
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado para userId: " + psychologistId));

        List<Appointment> appointments = appointmentRepo.findByPsychologistAndDateTimeBetween(
                psychologist, startDate.atStartOfDay(), endDate.atTime(23, 59));

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
            final LocalDate currentDate = date; // ← ahora sí es final para la lambda

            int dow = currentDate.getDayOfWeek().getValue() % 7;

            List<String> slots = profile.getWorkingHours().stream()
                    .filter(wh -> wh.getDayOfWeek() == dow)
                    .flatMap(wh -> {
                        if (wh.getStartTime().isAfter(wh.getEndTime()) || wh.getStartTime().equals(wh.getEndTime())) {
                            System.out.println("Horario inválido en fecha " + currentDate + ": " + wh);
                            return Stream.empty();
                        }

                        LocalTime slot = wh.getStartTime();
                        LocalTime limit = wh.getEndTime().minusMinutes(duration);
                        List<String> daySlots = new ArrayList<>();

                        while (!slot.isAfter(limit)) {
                            boolean isBooked = bookedByDay.getOrDefault(currentDate, Collections.emptyList()).contains(slot);
                            if (!isBooked) {
                                daySlots.add(slot.toString());
                            }
                            slot = slot.plusMinutes(duration);
                        }
                        return daySlots.stream();
                    })
                    .sorted()
                    .collect(Collectors.toList());

            available.put(currentDate, slots);
        }

        return available;
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


}
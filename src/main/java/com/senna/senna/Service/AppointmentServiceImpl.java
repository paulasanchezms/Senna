package com.senna.senna.Service;

import com.senna.senna.DTO.AppointmentDTO;
import com.senna.senna.Entity.Appointment;
import com.senna.senna.Entity.AppointmentStatus;
import com.senna.senna.Entity.User;
import com.senna.senna.Repository.AppointmentRepository;
import com.senna.senna.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public Appointment scheduleAppointment(String patientEmail, AppointmentDTO dto) {
        // Obtener el paciente y el psicólogo
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        User psychologist = userRepository.findById(dto.getPsychologistId())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado"));

        // Supongamos que quieres evitar solapamientos en ese mismo horario.
        // Define el rango de la cita.
        LocalDateTime start = dto.getDateTime();
        LocalDateTime end = start.plusMinutes(dto.getDuration());

        List<Appointment> conflicts = appointmentRepository
                .findByPsychologistAndDateTimeBetweenAndStatusNot(psychologist, start, end, AppointmentStatus.CANCELADA);
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("El psicólogo ya tiene una cita en ese horario");
        }

        // Crear la cita con estado PENDIENTE o el que corresponda
        Appointment appointment = Appointment.builder()
                .dateTime(dto.getDateTime())
                .duration(dto.getDuration())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : AppointmentStatus.PENDIENTE)
                .patient(patient)
                .psychologist(psychologist)
                .build();
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment updateAppointment(Long appointmentId, AppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + appointmentId));

        // Actualizar los campos editables
        appointment.setDateTime(dto.getDateTime());
        appointment.setDuration(dto.getDuration());
        appointment.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }
        // Opcional: Actualizar la asignación de psicólogo si es necesario, pero normalmente no se cambia.

        return appointmentRepository.save(appointment);
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + appointmentId));
        // Cambiar el estado a CANCELADA o eliminar la cita físicamente, según la estrategia.
        // Aquí optamos por cambiar el estado:
        appointment.setStatus(AppointmentStatus.CANCELADA);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsForPatient(String patientEmail) {
        // Obtener al paciente a partir del email
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con email: " + patientEmail));
        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public List<Appointment> getAppointmentsForPsychologist(String psychologistEmail) {
        // Obtener al psicólogo a partir del email
        User psychologist = userRepository.findByEmail(psychologistEmail)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con email: " + psychologistEmail));
        return appointmentRepository.findByPsychologist(psychologist);
    }
}
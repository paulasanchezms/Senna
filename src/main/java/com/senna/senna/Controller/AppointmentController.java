package com.senna.senna.Controller;

import com.senna.senna.DTO.AppointmentDTO;
import com.senna.senna.DTO.AppointmentResponseDTO;
import com.senna.senna.Entity.Appointment;
import com.senna.senna.Mapper.AppointmentMapper;
import com.senna.senna.Service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Crear (o agendar) una cita. Supongamos que este endpoint es usado por un paciente.
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> scheduleAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AppointmentDTO dto) {
        // Aquí pasamos el email del paciente para asignarlo a la cita.
        Appointment appointment = appointmentService.scheduleAppointment(userDetails.getUsername(), dto);
        AppointmentResponseDTO response = AppointmentMapper.toResponseDTO(appointment);
        return ResponseEntity.status(201).body(response);
    }

    // Actualizar una cita existente
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentDTO dto) {
        Appointment updated = appointmentService.updateAppointment(id, dto);
        AppointmentResponseDTO response = AppointmentMapper.toResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    // Cancelar una cita (cambiar su estado a CANCELADA)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // Consultar citas para un paciente (endpoint para un paciente)
    @GetMapping("/patient")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsForPatient(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Appointment> appointments = appointmentService.getAppointmentsForPatient(userDetails.getUsername());
        List<AppointmentResponseDTO> response = appointments.stream()
                .map(AppointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Consultar citas para un psicólogo
    @GetMapping("/psychologist")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsForPsychologist(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Appointment> appointments = appointmentService.getAppointmentsForPsychologist(userDetails.getUsername());
        List<AppointmentResponseDTO> response = appointments.stream()
                .map(AppointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
package com.senna.senna.Controller;

import com.senna.senna.DTO.AppointmentResponseDTO;
import com.senna.senna.DTO.CreateAppointmentDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.Appointment;
import com.senna.senna.Entity.AppointmentStatus;
import com.senna.senna.Entity.User;
import com.senna.senna.Mapper.AppointmentMapper;
import com.senna.senna.Repository.AppointmentRepository;
import com.senna.senna.Repository.UserRepository;
import com.senna.senna.Service.AppointmentServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de citas.
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentServiceImpl appointmentService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentServiceImpl appointmentService, UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> scheduleAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateAppointmentDTO dto) {
        User patient = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado: " + userDetails.getUsername()));

        dto.setPatientId(patient.getId());
        AppointmentResponseDTO response = appointmentService.scheduleAppointment(dto);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id, @RequestBody CreateAppointmentDTO dto) {
        AppointmentResponseDTO updated = appointmentService.updateAppointment(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsForPatient(@AuthenticationPrincipal UserDetails userDetails) {
        User patient = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado: " + userDetails.getUsername()));

        Long patientId = patient.getId();
        List<AppointmentResponseDTO> list = appointmentService.getAppointmentsForPatient(patientId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/psychologist")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsForPsychologist(@AuthenticationPrincipal UserDetails userDetails) {
        User psychologist = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + userDetails.getUsername()));

        Long psychologistId = psychologist.getId();
        List<AppointmentResponseDTO> list = appointmentService.getAppointmentsForPsychologist(psychologistId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/available-times")
    public ResponseEntity<List<String>> getAvailableTimes(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam("date") LocalDate date) {
        User psychologist = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + userDetails.getUsername()));

        Long psychologistId = psychologist.getId();
        List<String> slots = appointmentService.getAvailableTimes(psychologistId, date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/available-times/week")
    public ResponseEntity<Map<LocalDate, List<String>>> getAvailableTimesForWeek(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User psychologist = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + userDetails.getUsername()));

        Map<LocalDate, List<String>> slots = appointmentService.getAvailableTimesForWeek(psychologist.getId(), startDate, endDate);
        return ResponseEntity.ok(slots);
    }

    // ✅ Este es el único método para citas pendientes (autenticado, sin path variable)
    @GetMapping("/psychologist/pending")
    public List<AppointmentResponseDTO> getPendingAppointmentsForAuthenticatedPsychologist(@AuthenticationPrincipal UserDetails userDetails) {
        User psychologist = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado: " + userDetails.getUsername()));

        List<Appointment> appointments = appointmentRepository.findByPsychologistAndStatus(psychologist, AppointmentStatus.PENDIENTE);

        // ✅ Mapa para evitar duplicados por ID
        Map<Long, AppointmentResponseDTO> únicos = new LinkedHashMap<>();
        for (Appointment a : appointments) {
            únicos.putIfAbsent(a.getId(), AppointmentMapper.toDTO(a));
        }

        return new ArrayList<>(únicos.values());
    }

    @PostMapping("/{id}/accept")
    public void acceptAppointment(@PathVariable Long id) {
        appointmentService.acceptAppointment(id);
    }

    @PostMapping("/{id}/reject")
    public void rejectAppointment(@PathVariable Long id) {
        appointmentService.rejectAppointment(id);
    }

    @GetMapping("/psychologist/patients")
    public ResponseEntity<List<UserResponseDTO>> getPatientsOfPsychologist(@AuthenticationPrincipal UserDetails userDetails) {
        User psychologist = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado"));

        List<UserResponseDTO> patients = appointmentService.getPatientsForPsychologist(psychologist.getId());
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/psychologists/{id}/available-times")
    public ResponseEntity<List<String>> getAvailableTimesForPsychologistId(@PathVariable Long id,
                                                                           @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<String> slots = appointmentService.getAvailableTimes(id, date);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/psychologists/{id}/available-times/week")
    public ResponseEntity<Map<LocalDate, List<String>>> getAvailableTimesForWeekById(
            @PathVariable Long id,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<LocalDate, List<String>> slots = appointmentService.getAvailableTimesForWeek(id, startDate, endDate);
        return ResponseEntity.ok(slots);
    }
}
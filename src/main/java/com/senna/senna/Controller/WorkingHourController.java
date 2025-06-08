package com.senna.senna.Controller;

import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Service.WorkingHourService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para CRUD de franjas horarias de psicólogo.
 */
@RestController
@RequestMapping("/api/psychologists/{userId}/profile/hours")
@RequiredArgsConstructor
public class WorkingHourController {

    private final WorkingHourService hourService;

    /**
     * Reemplaza todas las franjas del psicólogo por las recibidas.
     */
    @PutMapping
    public ResponseEntity<List<WorkingHourDTO>> replaceHours(
            @PathVariable Long userId,
            @RequestBody List<WorkingHourDTO> hoursDto
    ) {
        // Borra y vuelve a crear todas las franjas de golpe
        hourService.replaceWorkingHours(userId, hoursDto);
        // Recupera la lista actualizada
        List<WorkingHourDTO> updated = hourService.getWorkingHours(userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Lista todas las franjas del perfil de un psicólogo.
     */
    @GetMapping
    public ResponseEntity<List<WorkingHourDTO>> listHours(@PathVariable Long userId) {
        List<WorkingHourDTO> list = hourService.getWorkingHours(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * Crea una nueva franja horaria.
     */
    @PostMapping
    public ResponseEntity<WorkingHourDTO> createHour(
            @PathVariable Long userId,
            @RequestBody WorkingHourDTO dto
    ) {
        WorkingHourDTO created = hourService.createWorkingHour(userId, dto);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Actualiza una franja existente (solo un slot).
     */
    @PutMapping("/{hourId}")
    public ResponseEntity<WorkingHourDTO> updateHour(
            @PathVariable Long userId,
            @PathVariable Long hourId,
            @RequestBody WorkingHourDTO dto
    ) {
        WorkingHourDTO updated = hourService.updateWorkingHour(userId, hourId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina una franja horaria.
     */
    @DeleteMapping("/{hourId}")
    public ResponseEntity<Void> deleteHour(
            @PathVariable Long userId,
            @PathVariable Long hourId
    ) {
        hourService.deleteWorkingHour(userId, hourId);
        return ResponseEntity.noContent().build();
    }

    // Devuelve la lista de horarios disponibles para un psicólogo en una fecha específica
    @GetMapping("/availability")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam Long psychologistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<String> slots = hourService.getAvailableSlots(psychologistId, date);
        return ResponseEntity.ok(slots);
    }
}
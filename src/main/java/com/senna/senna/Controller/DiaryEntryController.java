package com.senna.senna.Controller;

import com.senna.senna.DTO.DiaryEntryDTO;
import com.senna.senna.DTO.DiaryEntryResponseDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Mapper.DiaryEntryMapper;
import com.senna.senna.Service.DiaryEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para entradas de diario.
 */
@RestController
@RequestMapping("/api/diary")
public class DiaryEntryController {

    private final DiaryEntryService diaryService;

    public DiaryEntryController(DiaryEntryService diaryService) {
        this.diaryService = diaryService;
    }

    /**
     * Crea o actualiza la entrada del paciente autenticado para la fecha dada.
     */
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<DiaryEntryResponseDTO> saveEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DiaryEntryDTO dto) {
        DiaryEntry saved = diaryService.saveEntry(userDetails.getUsername(), dto);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(saved);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas las entradas del paciente autenticado.
     */
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping
    public ResponseEntity<List<DiaryEntryResponseDTO>> getAllEntries(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DiaryEntry> entries = diaryService.getAllEntries(userDetails.getUsername());
        List<DiaryEntryResponseDTO> response = entries.stream()
                .map(DiaryEntryMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene la entrada del paciente por fecha (ISO yyyy-MM-dd).
     */
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/date/{date}")
    public ResponseEntity<DiaryEntryResponseDTO> getEntryByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String date) {
        DiaryEntry entry = diaryService.getEntryByDate(userDetails.getUsername(), date);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(entry);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza una entrada existente del diario (por id).
     */
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/entry/{id}")
    public ResponseEntity<DiaryEntryResponseDTO> updateEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody DiaryEntryDTO dto) {
        DiaryEntry updated = diaryService.updateEntry(userDetails.getUsername(), id, dto);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una entrada del diario (por id).
     */
    @PreAuthorize("hasRole('PATIENT')")
    @DeleteMapping("/entry/{id}")
    public ResponseEntity<Void> deleteEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        diaryService.deleteEntry(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para que un psicólogo consulte las entradas del diario de un paciente asignado.
     */
    @PreAuthorize("hasRole('PSYCHOLOGIST')")
    @GetMapping("/psychologist/patient/{patientId}")
    public ResponseEntity<List<DiaryEntryResponseDTO>> getEntriesForPatient(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long patientId) {
        List<DiaryEntry> entries = diaryService.getEntriesForPatient(userDetails.getUsername(), patientId);
        List<DiaryEntryResponseDTO> response = entries.stream()
                .map(DiaryEntryMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
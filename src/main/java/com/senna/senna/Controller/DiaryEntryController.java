package com.senna.senna.Controller;

import com.senna.senna.DTO.DiaryEntryDTO;
import com.senna.senna.DTO.DiaryEntryResponseDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Mapper.DiaryEntryMapper;
import com.senna.senna.Service.DiaryEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diary")
public class DiaryEntryController {

    private final DiaryEntryService diaryService;

    public DiaryEntryController(DiaryEntryService diaryService) {
        this.diaryService = diaryService;
    }

    // Crear una nueva entrada del diario
    @PostMapping
    public ResponseEntity<DiaryEntryResponseDTO> saveEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DiaryEntryDTO dto) {
        DiaryEntry saved = diaryService.saveEntry(userDetails.getUsername(), dto);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(saved);
        return ResponseEntity.ok(response);
    }

    // Obtener todas las entradas del diario para el usuario autenticado
    @GetMapping
    public ResponseEntity<List<DiaryEntryResponseDTO>> getAllEntries(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DiaryEntry> entries = diaryService.getAllEntries(userDetails.getUsername());
        List<DiaryEntryResponseDTO> response = entries.stream()
                .map(DiaryEntryMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Obtener la entrada del diario para una fecha específica (formato ISO: yyyy-MM-dd)
    @GetMapping("/date/{date}")
    public ResponseEntity<DiaryEntryResponseDTO> getEntryByDate(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String date) {
        DiaryEntry entry = diaryService.getEntryByDate(userDetails.getUsername(), date);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(entry);
        return ResponseEntity.ok(response);
    }

    // Actualizar una entrada existente del diario (por id)
    @PutMapping("/entry/{id}")
    public ResponseEntity<DiaryEntryResponseDTO> updateEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody DiaryEntryDTO dto) {
        DiaryEntry updated = diaryService.updateEntry(userDetails.getUsername(), id, dto);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    // Eliminar una entrada del diario (por id)
    @DeleteMapping("/entry/{id}")
    public ResponseEntity<Void> deleteEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        diaryService.deleteEntry(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para que un psicólogo consulte las entradas del diario de un paciente asignado
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
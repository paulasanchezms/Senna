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

    // Crear o actualizar una entrada del diario
    @PostMapping
    public ResponseEntity<DiaryEntryResponseDTO> saveEntry(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestBody DiaryEntryDTO dto) {
        // Se pasa directamente el email del usuario autenticado
        DiaryEntry saved = diaryService.saveEntry(userDetails.getUsername(), dto);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(saved);
        return ResponseEntity.ok(response);
    }

    // Obtener todas las entradas del diario para el usuario
    @GetMapping
    public ResponseEntity<List<DiaryEntryResponseDTO>> getAllEntries(@AuthenticationPrincipal UserDetails userDetails) {
        List<DiaryEntry> entries = diaryService.getAllEntries(userDetails.getUsername());
        List<DiaryEntryResponseDTO> response = entries.stream()
                .map(DiaryEntryMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Obtener la entrada del diario de una fecha específica (formato ISO: yyyy-MM-dd)
    @GetMapping("/{date}")
    public ResponseEntity<DiaryEntryResponseDTO> getEntryByDate(@AuthenticationPrincipal UserDetails userDetails,
                                                                @PathVariable String date) {
        DiaryEntry entry = diaryService.getEntryByDate(userDetails.getUsername(), date);
        DiaryEntryResponseDTO response = DiaryEntryMapper.toResponseDTO(entry);
        return ResponseEntity.ok(response);
    }

    // Endpoint para que un psicólogo consulte las entradas de un paciente asignado
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
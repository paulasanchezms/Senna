package com.senna.senna.Controller;

import com.senna.senna.DTO.PsychologistProfileDTO;
import com.senna.senna.DTO.CreatePsychologistProfileDTO;
import com.senna.senna.DTO.UpdatePsychologistProfileDTO;
import com.senna.senna.DTO.WorkingHourDTO;
import com.senna.senna.Service.PsychologistProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión del perfil profesional de psicólogos.
 */
@RestController
@RequestMapping("/api/psychologists/{userId}/profile")
@RequiredArgsConstructor
public class PsychologistProfileController {

    private final PsychologistProfileService profileService;

    /**
     * Obtiene el perfil profesional de un psicólogo.
     */
    @GetMapping
    public ResponseEntity<PsychologistProfileDTO> getProfile(@PathVariable Long userId) {
        PsychologistProfileDTO dto = profileService.getProfile(userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Crea el perfil profesional (duración, precio, especialidad, ubicación y franjas).
     */
    @PostMapping
    public ResponseEntity<PsychologistProfileDTO> createProfile(
            @PathVariable Long userId,
            @RequestBody CreatePsychologistProfileDTO dto
    ) {
        PsychologistProfileDTO created = profileService.createProfile(userId, dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Actualiza datos generales del perfil (sin modificar franjas).
     */
    @PutMapping
    public ResponseEntity<PsychologistProfileDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdatePsychologistProfileDTO dto
    ) {
        PsychologistProfileDTO updated = profileService.updateProfile(userId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Actualiza únicamente las franjas horarias del psicólogo.
     */
    @PutMapping("/hours")
    public ResponseEntity<Void> updateWorkingHours(
            @PathVariable Long userId,
            @RequestBody List<WorkingHourDTO> hours
    ) {
        profileService.updateWorkingHours(userId, hours);
        return ResponseEntity.noContent().build();
    }

}

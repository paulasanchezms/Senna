package com.senna.senna.Mapper;

import com.senna.senna.DTO.DiaryEntryResponseDTO;
import com.senna.senna.DTO.MoodDTO;
import com.senna.senna.DTO.SymptomDTO;
import com.senna.senna.DTO.UserResponseDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.Mood;
import com.senna.senna.Entity.Symptom;

import java.util.List;
import java.util.stream.Collectors;

public class DiaryEntryMapper {

    // Convierte una entidad DiaryEntry a su DTO de respuesta
    public static DiaryEntryResponseDTO toResponseDTO(DiaryEntry entry) {
        DiaryEntryResponseDTO dto = new DiaryEntryResponseDTO();
        dto.setId(entry.getId());
        dto.setDate(entry.getDate());
        dto.setNotes(entry.getNotes());
        dto.setMoodLevel(entry.getMoodLevel());

        // Map user
        UserResponseDTO userDTO = UserMapper.toResponseDTO(entry.getUser());
        dto.setUser(userDTO);

        // Map moods
        List<MoodDTO> moodDTOs = entry.getMoods()
                .stream()
                .map(DiaryEntryMapper::toMoodDTO)
                .collect(Collectors.toList());
        dto.setMoods(moodDTOs);

        // Map symptoms
        List<SymptomDTO> symptomDTOs = entry.getSymptoms()
                .stream()
                .map(DiaryEntryMapper::toSymptomDTO)
                .collect(Collectors.toList());
        dto.setSymptoms(symptomDTOs);

        return dto;
    }

    // Convierte una entidad Mood a MoodDTO
    private static MoodDTO toMoodDTO(Mood mood) {
        MoodDTO dto = new MoodDTO();
        dto.setId(mood.getId());
        dto.setName(mood.getName());
        dto.setIcon(mood.getIcon());
        return dto;
    }

    // Convierte una entidad Symptom a SymptomDTO
    private static SymptomDTO toSymptomDTO(Symptom symptom) {
        SymptomDTO dto = new SymptomDTO();
        dto.setId(symptom.getId());
        dto.setName(symptom.getName());
        dto.setIcon(symptom.getIcon());
        return dto;
    }
}
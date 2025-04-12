package com.senna.senna.Mapper;

import com.senna.senna.DTO.DiaryEntryResponseDTO;
import com.senna.senna.Entity.DiaryEntry;

public class DiaryEntryMapper {

    public static DiaryEntryResponseDTO toResponseDTO(DiaryEntry entry) {
        DiaryEntryResponseDTO dto = new DiaryEntryResponseDTO();
        dto.setId(entry.getId());
        dto.setDate(entry.getDate());
        dto.setMood(entry.getMood());
        dto.setSymptoms(entry.getSymptoms());
        dto.setNotes(entry.getNotes());
        dto.setUser(UserMapper.toResponseDTO(entry.getUser()));
        return dto;
    }
}
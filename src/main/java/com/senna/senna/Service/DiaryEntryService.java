package com.senna.senna.Service;

import com.senna.senna.DTO.DiaryEntryDTO;
import com.senna.senna.Entity.DiaryEntry;
import java.util.List;

public interface DiaryEntryService {
    DiaryEntry saveEntry(String userEmail, DiaryEntryDTO dto);
    List<DiaryEntry> getAllEntries(String userEmail);
    DiaryEntry getEntryByDate(String userEmail, String date);
    List<DiaryEntry> getEntriesForPatient(String psychologistEmail, Long patientId);
    DiaryEntry updateEntry(String userEmail, Long entryId, DiaryEntryDTO dto);
    void deleteEntry(String userEmail, Long entryId);
}
package com.senna.senna.Service;

import com.senna.senna.DTO.DiaryEntryDTO;
import com.senna.senna.Entity.*;
import com.senna.senna.Repository.DiaryEntryRepository;
import com.senna.senna.Repository.MoodRepository;
import com.senna.senna.Repository.SymptomRepository;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DiaryEntryServiceImpl implements DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SymptomRepository symptomRepository;
    private final MoodRepository moodRepository;

    public DiaryEntryServiceImpl(DiaryEntryRepository diaryEntryRepository,
                                 UserService userService,
                                 UserRepository userRepository,
                                 SymptomRepository symptomRepository,
                                 MoodRepository moodRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.symptomRepository = symptomRepository;
        this.moodRepository = moodRepository;
    }

    @Override
    public DiaryEntry saveEntry(String userEmail, DiaryEntryDTO dto) {
        User user = userService.findByEmail(userEmail);

        List<Long> symptomIds = dto.getSymptomIds() != null ? dto.getSymptomIds() : Collections.emptyList();
        List<Long> moodIds = dto.getMoodIds() != null ? dto.getMoodIds() : Collections.emptyList();

        Set<Symptom> symptoms = new HashSet<>(symptomRepository.findAllById(symptomIds));
        Set<Mood> moods = new HashSet<>(moodRepository.findAllById(moodIds));

        Optional<DiaryEntry> existingEntry = diaryEntryRepository.findByUserAndDate(user, dto.getDate());
        if (existingEntry.isPresent()) {
            DiaryEntry entry = existingEntry.get();
            entry.setSymptoms(symptoms);
            entry.setMoods(moods);
            entry.setNotes(dto.getNotes());
            entry.setMoodLevel(dto.getMoodLevel());
            return diaryEntryRepository.save(entry);
        } else {
            DiaryEntry entry = DiaryEntry.builder()
                    .user(user)
                    .date(dto.getDate())
                    .symptoms(symptoms)
                    .moods(moods)
                    .notes(dto.getNotes())
                    .moodLevel(dto.getMoodLevel())
                    .build();
            return diaryEntryRepository.save(entry);
        }
    }

    @Override
    public List<DiaryEntry> getAllEntries(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return diaryEntryRepository.findByUser(user);
    }

    @Override
    public DiaryEntry updateEntry(String userEmail, Long entryId, DiaryEntryDTO dto) {
        User user = userService.findByEmail(userEmail);
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada con id: " + entryId));

        if (!entry.getUser().getId_user().equals(user.getId_user())) {
            throw new IllegalArgumentException("No tienes permiso para editar esta entrada");
        }

        List<Long> symptomIds = dto.getSymptomIds() != null ? dto.getSymptomIds() : Collections.emptyList();
        List<Long> moodIds = dto.getMoodIds() != null ? dto.getMoodIds() : Collections.emptyList();

        Set<Symptom> symptoms = new HashSet<>(symptomRepository.findAllById(symptomIds));
        Set<Mood> moods = new HashSet<>(moodRepository.findAllById(moodIds));

        entry.setSymptoms(symptoms);
        entry.setMoods(moods);
        entry.setNotes(dto.getNotes());
        entry.setMoodLevel(dto.getMoodLevel());

        return diaryEntryRepository.save(entry);
    }

    @Override
    public void deleteEntry(String userEmail, Long entryId) {
        User user = userService.findByEmail(userEmail);
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada con id: " + entryId));
        if (!entry.getUser().getId_user().equals(user.getId_user())) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta entrada");
        }
        diaryEntryRepository.delete(entry);
    }

    @Override
    public DiaryEntry getEntryByDate(String userEmail, String date) {
        User user = userService.findByEmail(userEmail);
        LocalDate localDate = LocalDate.parse(date);
        return diaryEntryRepository.findByUserAndDate(user, localDate)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada para la fecha " + date));
    }

    @Override
    public List<DiaryEntry> getEntriesForPatient(String psychologistEmail, Long patientId) {
        User psychologist = userService.findByEmail(psychologistEmail);
        if (psychologist == null) {
            throw new EntityNotFoundException("Psicólogo no encontrado con email: " + psychologistEmail);
        }
        if (!psychologist.getRole().equals(Role.PSYCHOLOGIST)) {
            throw new IllegalArgumentException("El usuario no tiene rol PSYCHOLOGIST");
        }
        User patient = userService.findByIdEntity(patientId);
        if (patient == null) {
            throw new EntityNotFoundException("Paciente no encontrado con id: " + patientId);
        }
        if (!psychologist.getPatients().contains(patient)) {
            throw new IllegalArgumentException("El paciente no está asignado al psicólogo");
        }
        return diaryEntryRepository.findByUser(patient);
    }
}
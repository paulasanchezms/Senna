package com.senna.senna.Service;

import com.senna.senna.DTO.DiaryEntryDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.Role;
import com.senna.senna.Entity.Symptom;
import com.senna.senna.Entity.Mood;
import com.senna.senna.Entity.User;
import com.senna.senna.Repository.DiaryEntryRepository;
import com.senna.senna.Repository.SymptomRepository;
import com.senna.senna.Repository.MoodRepository;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryEntryServiceImpl implements DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserRepository userRepository;
    private final SymptomRepository symptomRepository;
    private final MoodRepository moodRepository;

    /**
     * Guarda una nueva entrada o actualiza una existente para una fecha concreta.
     */
    @Override
    public DiaryEntry saveEntry(String userEmail, DiaryEntryDTO dto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + userEmail));

        List<Long> symptomIds = dto.getSymptomIds() != null ? dto.getSymptomIds() : Collections.emptyList();
        List<Long> moodIds = dto.getMoodIds() != null ? dto.getMoodIds() : Collections.emptyList();

        Set<Symptom> symptoms = new HashSet<>(symptomRepository.findAllById(symptomIds));
        Set<Mood> moods = new HashSet<>(moodRepository.findAllById(moodIds));

        LocalDate date = dto.getDate();
        DiaryEntry entry = diaryEntryRepository.findByUserAndDate(user, date)
                .orElseGet(() -> DiaryEntry.builder()
                        .user(user)
                        .date(date)
                        .build()
                );

        entry.setSymptoms(symptoms);
        entry.setMoods(moods);
        entry.setNotes(dto.getNotes());
        entry.setMoodLevel(dto.getMoodLevel());

        return diaryEntryRepository.save(entry);
    }

    /**
     * Obtiene todas las entradas de diario del usuario autenticado.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DiaryEntry> getAllEntries(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + userEmail));
        return diaryEntryRepository.findByUser(user);
    }

    /**
     * Obtiene una entrada específica según la fecha para el usuario autenticado.
     */
    @Override
    @Transactional(readOnly = true)
    public DiaryEntry getEntryByDate(String userEmail, String date) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + userEmail));
        LocalDate localDate = LocalDate.parse(date);
        return diaryEntryRepository.findByUserAndDate(user, localDate)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada para la fecha " + date));
    }

    /**
     * Permite a un psicólogo acceder a las entradas de un paciente asignado.
     */
    @Override
    public List<DiaryEntry> getEntriesForPatient(String psychologistEmail, Long patientId) {
        User psychologist = userRepository.findByEmail(psychologistEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + psychologistEmail));
        if (psychologist.getRole() != Role.PSYCHOLOGIST) {
            throw new IllegalArgumentException("El usuario debe tener rol PSYCHOLOGIST");
        }
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + patientId));
       if (!psychologist.getPatients().contains(patient)) {
           throw new IllegalArgumentException("El paciente no está asignado al psicólogo");
        }
        return diaryEntryRepository.findByUser(patient);
    }

    /**
     * Actualiza una entrada existente si pertenece al usuario autenticado.
     */
    @Override
    public DiaryEntry updateEntry(String userEmail, Long entryId, DiaryEntryDTO dto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + userEmail));
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada con id: " + entryId));
        if (!entry.getUser().getId().equals(user.getId())) {
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

    /**
     * Elimina una entrada si pertenece al usuario autenticado.
     */
    @Override
    public void deleteEntry(String userEmail, Long entryId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + userEmail));
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada con id: " + entryId));
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta entrada");
        }
        diaryEntryRepository.delete(entry);
    }
}

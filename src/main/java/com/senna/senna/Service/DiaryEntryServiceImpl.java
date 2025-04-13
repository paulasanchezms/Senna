package com.senna.senna.Service;

import com.senna.senna.DTO.DiaryEntryDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.Role;
import com.senna.senna.Entity.User;
import com.senna.senna.Repository.DiaryEntryRepository;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DiaryEntryServiceImpl implements DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserService userService;  // Usado para obtener entidades User (paciente o psicólogo)
    private final UserRepository userRepository; // Para consultas puntuales, en caso de necesitar obtener paciente por id

    public DiaryEntryServiceImpl(DiaryEntryRepository diaryEntryRepository, UserService userService, UserRepository userRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public DiaryEntry saveEntry(String userEmail, DiaryEntryDTO dto) {
        // Obtenemos la entidad completa del usuario (paciente) a partir del email
        User user = userService.findByEmail(userEmail);
        // Si ya existe una entrada para la fecha, actualizamos; sino, creamos una nueva
        Optional<DiaryEntry> existingEntry = diaryEntryRepository.findByUserAndDate(user, dto.getDate());
        if (existingEntry.isPresent()) {
            DiaryEntry entry = existingEntry.get();
            entry.setMood(dto.getMood());
            entry.setSymptoms(dto.getSymptoms());
            entry.setNotes(dto.getNotes());
            return diaryEntryRepository.save(entry);
        } else {
            DiaryEntry entry = DiaryEntry.builder()
                    .user(user)
                    .date(dto.getDate())
                    .mood(dto.getMood())
                    .symptoms(dto.getSymptoms())
                    .notes(dto.getNotes())
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
        // Obtener el usuario autenticado (paciente)
        User user = userService.findByEmail(userEmail);
        // Buscar la entrada por su ID
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada con id: " + entryId));
        // Verificar que la entrada pertenece al usuario
        if (!entry.getUser().getId_user().equals(user.getId_user())) {
            throw new IllegalArgumentException("No tienes permiso para editar esta entrada");
        }
        // Actualizar campos de la entrada y guardarla
        entry.setMood(dto.getMood());
        entry.setSymptoms(dto.getSymptoms());
        entry.setNotes(dto.getNotes());
        // Opcional: podrías permitir actualizar la fecha si así lo deseas, pero ten en cuenta que la fecha se usa para identificar la entrada.
        // entry.setDate(dto.getDate());
        return diaryEntryRepository.save(entry);
    }

    @Override
    public void deleteEntry(String userEmail, Long entryId) {
        // Obtener usuario autenticado (paciente)
        User user = userService.findByEmail(userEmail);
        // Buscar la entrada por ID
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la entrada con id: " + entryId));
        // Verificar que la entrada pertenece al usuario
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
        // Obtener la entidad del psicólogo a partir del email
        User psychologist = userService.findByEmail(psychologistEmail);
        if (psychologist == null) {
            throw new EntityNotFoundException("Psicólogo no encontrado con email: " + psychologistEmail);
        }
        if (!psychologist.getRole().equals(Role.PSYCHOLOGIST)) {
            throw new IllegalArgumentException("El usuario no tiene rol PSYCHOLOGIST");
        }
        // Obtener la entidad del paciente usando su id
        User patient = userService.findByIdEntity(patientId);
        if (patient == null) {
            throw new EntityNotFoundException("Paciente no encontrado con id: " + patientId);
        }
        // Validar que el paciente esté asignado al psicólogo
        if (!psychologist.getPatients().contains(patient)) {
            throw new IllegalArgumentException("El paciente no está asignado al psicólogo");
        }
        return diaryEntryRepository.findByUser(patient);
    }
}
package com.senna.senna.Repository;

import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {

    /**
     * Devuelve todas las entradas del diario asociadas a un usuario.
     */
    List<DiaryEntry> findByUser(User user);

    /**
     * Devuelve una entrada del diario de un usuario en una fecha concreta, si existe.
     */
    Optional<DiaryEntry> findByUserAndDate(User user, LocalDate date);

    /**
     * Devuelve las entradas del diario de un usuario dentro de un rango de fechas.
     * Útil para generar estadísticas semanales o mensuales.
     */
    List<DiaryEntry> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
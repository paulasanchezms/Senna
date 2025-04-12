package com.senna.senna.Repository;

import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long> {
    List<DiaryEntry> findByUser(User user);
    Optional<DiaryEntry> findByUserAndDate(User user, LocalDate date);
}
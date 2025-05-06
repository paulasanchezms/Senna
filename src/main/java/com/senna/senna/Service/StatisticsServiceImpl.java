package com.senna.senna.Service;

import com.senna.senna.DTO.StatisticsResponseDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.User;
import com.senna.senna.Repository.DiaryEntryRepository;
import com.senna.senna.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public StatisticsResponseDTO getWeeklyStatistics(String userEmail) {
        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.ISO;
        int currentWeek = today.get(wf.weekOfYear());
        int currentYear = today.getYear();
        return getWeeklyStatistics(userEmail, currentYear, currentWeek);
    }


    @Transactional(readOnly = true)
    public StatisticsResponseDTO getWeeklyStatistics(String userEmail, int year, int weekOfYear) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + userEmail));

        WeekFields wf = WeekFields.ISO;
        LocalDate monday = LocalDate.of(year, 1, 4)
                .with(wf.weekOfYear(), weekOfYear)
                .with(wf.dayOfWeek(), DayOfWeek.MONDAY.getValue());
        LocalDate sunday = monday.plusDays(6);

        List<DiaryEntry> entries = diaryEntryRepository
                .findByUserAndDateBetween(user, monday, sunday);

        return calculateStatistics(entries, true, monday, sunday);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsResponseDTO getMonthlyStatistics(String userEmail) {
        LocalDate today = LocalDate.now();
        return getMonthlyStatistics(userEmail, today.getYear(), today.getMonthValue());
    }


    @Transactional(readOnly = true)
    public StatisticsResponseDTO getMonthlyStatistics(String userEmail, int year, int month) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + userEmail));

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startOfMonth = ym.atDay(1);
        LocalDate endOfMonth   = ym.atEndOfMonth();

        List<DiaryEntry> entries = diaryEntryRepository
                .findByUserAndDateBetween(user, startOfMonth, endOfMonth);

        return calculateStatistics(entries, false, startOfMonth, endOfMonth);
    }

    /**
     * Lógica común de cálculo de estadísticas.
     */
    private StatisticsResponseDTO calculateStatistics(
            List<DiaryEntry> entries,
            boolean isWeekly,
            LocalDate start,
            LocalDate end
    ) {
        int totalEntries = entries.size();
        Map<String, Long> moodCounts = new HashMap<>();
        Map<String, Long> symptomCounts = new HashMap<>();

        entries.forEach(e -> {
            e.getMoods().forEach(m ->
                    moodCounts.merge(m.getName(), 1L, Long::sum)
            );
            e.getSymptoms().forEach(s ->
                    symptomCounts.merge(s.getName(), 1L, Long::sum)
            );
        });

        int periodLength = (int) (end.toEpochDay() - start.toEpochDay()) + 1;
        List<Integer> moodLevels = Collections.nCopies(periodLength, 0).stream().collect(Collectors.toList());
        List<Integer> counts     = Collections.nCopies(periodLength, 0).stream().collect(Collectors.toList());

        for (DiaryEntry e : entries) {
            if (e.getMoodLevel() == null) continue;
            int idx;
            if (isWeekly) {
                idx = e.getDate().getDayOfWeek().getValue() - 1;
            } else {
                idx = e.getDate().getDayOfMonth() - 1;
            }
            moodLevels.set(idx, moodLevels.get(idx) + e.getMoodLevel());
            counts.set(idx,     counts.get(idx)     + 1);
        }

        for (int i = 0; i < periodLength; i++) {
            if (counts.get(i) > 0) {
                moodLevels.set(i, moodLevels.get(i) / counts.get(i));
            }
        }

        List<Integer> weeklyMoodLevels  = isWeekly ? moodLevels : Collections.emptyList();
        List<Integer> monthlyMoodLevels = isWeekly ? Collections.emptyList() : moodLevels;

        return new StatisticsResponseDTO(
                totalEntries,
                moodCounts,
                symptomCounts,
                weeklyMoodLevels,
                monthlyMoodLevels,
                Collections.emptyList(),
                periodLength
        );
    }
}

package com.senna.senna.Service;

import com.senna.senna.DTO.StatisticsResponseDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.User;
import com.senna.senna.Repository.DiaryEntryRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserService userService;

    public StatisticsServiceImpl(DiaryEntryRepository diaryEntryRepository, UserService userService) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userService = userService;
    }

    @Override
    public StatisticsResponseDTO getWeeklyStatistics(String userEmail) {
        User user = userService.findByEmail(userEmail);
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<DiaryEntry> entries = diaryEntryRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);

        return calculateStatistics(entries, true);
    }

    @Override
    public StatisticsResponseDTO getMonthlyStatistics(String userEmail) {
        User user = userService.findByEmail(userEmail);
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        List<DiaryEntry> entries = diaryEntryRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth);

        return calculateStatistics(entries, false);
    }

    private StatisticsResponseDTO calculateStatistics(List<DiaryEntry> entries, boolean isWeekly) {
        int totalEntries = entries.size();
        Map<String, Long> moodCounts = new HashMap<>();
        Map<String, Long> symptomCounts = new HashMap<>();

        // Preparar listas para semanal (7 días) y mensual (31 días)
        List<Integer> weeklyMoodLevels = new ArrayList<>(Collections.nCopies(7, 0));
        List<Integer> weeklyCounts = new ArrayList<>(Collections.nCopies(7, 0));

        List<Integer> monthlyMoodLevels = new ArrayList<>(Collections.nCopies(31, 0));
        List<Integer> monthlyCounts = new ArrayList<>(Collections.nCopies(31, 0));

        for (DiaryEntry entry : entries) {
            // Contadores globales
            entry.getMoods().forEach(mood ->
                    moodCounts.put(mood.getName(), moodCounts.getOrDefault(mood.getName(), 0L) + 1)
            );
            entry.getSymptoms().forEach(symptom ->
                    symptomCounts.put(symptom.getName(), symptomCounts.getOrDefault(symptom.getName(), 0L) + 1)
            );

            // Repartir moodLevel
            if (entry.getMoodLevel() != null) {
                int dayOfWeek = entry.getDate().getDayOfWeek().getValue() - 1;  // lunes=0
                int dayOfMonth = entry.getDate().getDayOfMonth() - 1;           // día 1 = 0

                weeklyMoodLevels.set(dayOfWeek, weeklyMoodLevels.get(dayOfWeek) + entry.getMoodLevel());
                weeklyCounts.set(dayOfWeek, weeklyCounts.get(dayOfWeek) + 1);

                monthlyMoodLevels.set(dayOfMonth, monthlyMoodLevels.get(dayOfMonth) + entry.getMoodLevel());
                monthlyCounts.set(dayOfMonth, monthlyCounts.get(dayOfMonth) + 1);
            }
        }

        // Calcular promedio semanal
        for (int i = 0; i < weeklyMoodLevels.size(); i++) {
            if (weeklyCounts.get(i) > 0) {
                weeklyMoodLevels.set(i, weeklyMoodLevels.get(i) / weeklyCounts.get(i));
            }
        }

        // Calcular promedio mensual
        for (int i = 0; i < monthlyMoodLevels.size(); i++) {
            if (monthlyCounts.get(i) > 0) {
                monthlyMoodLevels.set(i, monthlyMoodLevels.get(i) / monthlyCounts.get(i));
            }
        }

        return new StatisticsResponseDTO(
                totalEntries,
                moodCounts,
                symptomCounts,
                weeklyMoodLevels,
                monthlyMoodLevels,
                Collections.emptyList()
        );
    }
}
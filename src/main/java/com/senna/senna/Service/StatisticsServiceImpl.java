package com.senna.senna.Service;

import com.senna.senna.DTO.StatisticsResponseDTO;
import com.senna.senna.Entity.DiaryEntry;
import com.senna.senna.Entity.User;
import com.senna.senna.Repository.DiaryEntryRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserService userService;

    public StatisticsServiceImpl(DiaryEntryRepository diaryEntryRepository, UserService userService) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userService = userService;
    }

    // ----------------– SEMANA ------------------

    /** Devuelve la estadística de la semana actual */
    @Override
    public StatisticsResponseDTO getWeeklyStatistics(String userEmail) {
        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.ISO;
        int currentWeek = today.get(wf.weekOfYear());
        int currentYear = today.getYear();
        return getWeeklyStatistics(userEmail, currentYear, currentWeek);
    }

    /**
     * Navega a cualquier semana del año
     * @param userEmail   email del usuario
     * @param year        año (ej. 2025)
     * @param weekOfYear  número de semana ISO (1–53)
     */
    public StatisticsResponseDTO getWeeklyStatistics(String userEmail, int year, int weekOfYear) {
        User user = userService.findByEmail(userEmail);

        WeekFields wf = WeekFields.ISO;

        LocalDate monday = LocalDate
                .of(year, 1, 4)   // 4 de enero siempre pertenece a la semana 1 ISO
                .with(wf.weekOfYear(), weekOfYear)
                .with(wf.dayOfWeek(), DayOfWeek.MONDAY.getValue());
        LocalDate sunday = monday.plusDays(6);

        List<DiaryEntry> entries = diaryEntryRepository
                .findByUserAndDateBetween(user, monday, sunday);

        return calculateStatistics(entries, true, monday, sunday);
    }
    // --------------- MES ----------------------

    /** Devuelve la estadística del mes actual */
    @Override
    public StatisticsResponseDTO getMonthlyStatistics(String userEmail) {
        LocalDate today = LocalDate.now();
        return getMonthlyStatistics(userEmail, today.getYear(), today.getMonthValue());
    }

    /**
     * Navega a cualquier mes de un año
     * @param userEmail   email del usuario
     * @param year        año (ej. 2025)
     * @param month       mes 1–12
     */
    public StatisticsResponseDTO getMonthlyStatistics(String userEmail, int year, int month) {
        User user = userService.findByEmail(userEmail);

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startOfMonth = ym.atDay(1);
        LocalDate endOfMonth   = ym.atEndOfMonth();

        List<DiaryEntry> entries = diaryEntryRepository
                .findByUserAndDateBetween(user, startOfMonth, endOfMonth);

        return calculateStatistics(entries, false, startOfMonth, endOfMonth);
    }

    // ------------- CÁLCULO COMÚN ----------------

    /**
     * @param entries     todas las entradas en el rango
     * @param isWeekly    true si es semanal (7 días), false si es mensual
     * @param start       inicio del periodo (lunes o día 1 del mes)
     * @param end         fin del periodo (domingo o último día del mes)
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

        // Contadores de moods/síntomas
        for (DiaryEntry e : entries) {
            e.getMoods().forEach(m ->
                    moodCounts.merge(m.getName(), 1L, Long::sum)
            );
            e.getSymptoms().forEach(s ->
                    symptomCounts.merge(s.getName(), 1L, Long::sum)
            );
        }

        // Inicializamos arrays
        int periodLength = (int) (end.toEpochDay() - start.toEpochDay()) + 1;
        // si isWeekly==true → periodLength==7
        List<Integer> moodLevels  = new ArrayList<>(Collections.nCopies(periodLength, 0));
        List<Integer> counts      = new ArrayList<>(Collections.nCopies(periodLength, 0));

        // Sumamos niveles por día índice
        for (DiaryEntry e : entries) {
            if (e.getMoodLevel() == null) continue;
            int idx;
            if (isWeekly) {
                // lunes = índice 0  … domingo = 6
                idx = e.getDate().getDayOfWeek().getValue() - 1;
            } else {
                // día del mes 1→idx0, 2→idx1…
                idx = e.getDate().getDayOfMonth() - 1;
            }
            moodLevels.set(idx, moodLevels.get(idx) + e.getMoodLevel());
            counts.set(idx,     counts.get(idx)     + 1);
        }

        // Convertimos sumas a promedios
        for (int i = 0; i < periodLength; i++) {
            if (counts.get(i) > 0) {
                moodLevels.set(i, moodLevels.get(i) / counts.get(i));
            }
        }

        // Para la respuesta, rellenamos explícitamente ambos arrays
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
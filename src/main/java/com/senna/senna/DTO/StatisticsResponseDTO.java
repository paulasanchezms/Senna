package com.senna.senna.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponseDTO {
    private int totalEntries;
    private Map<String, Long> moodCounts;
    private Map<String, Long> symptomCounts;
    private List<Integer> weeklyMoodLevels;
    private List<Integer> monthlyMoodLevels;
    private List<Integer> moodLevels;
    private int daysInMonth;
}
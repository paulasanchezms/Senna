package com.senna.senna.Service;

import com.senna.senna.DTO.StatisticsResponseDTO;

public interface StatisticsService {
    StatisticsResponseDTO getWeeklyStatistics(String userEmail);
    StatisticsResponseDTO getMonthlyStatistics(String userEmail);
}
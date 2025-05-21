package com.senna.senna.Service;

import com.senna.senna.DTO.StatisticsResponseDTO;

public interface StatisticsService {
    StatisticsResponseDTO getWeeklyStatistics(String userEmail);
    StatisticsResponseDTO getWeeklyStatistics(String userEmail, Long patientId);
    StatisticsResponseDTO getWeeklyStatistics(String userEmail, int year, int weekOfYear, Long patientId);

    StatisticsResponseDTO getMonthlyStatistics(String userEmail);
    StatisticsResponseDTO getMonthlyStatistics(String userEmail, Long patientId);
    StatisticsResponseDTO getMonthlyStatistics(String userEmail, int year, int month, Long patientId);
}
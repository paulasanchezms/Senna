package com.senna.senna.Controller;

import com.senna.senna.DTO.StatisticsResponseDTO;
import com.senna.senna.Service.StatisticsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsServiceImpl statisticsService;

    public StatisticsController(StatisticsServiceImpl statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * /api/statistics/weekly?year=2025&week=18
     * si no mandas year+week devuelve la semana actual
     */
    @GetMapping("/weekly")
    public ResponseEntity<StatisticsResponseDTO> getWeeklyStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "week", required = false) Integer week
    ) {
        String email = userDetails.getUsername();
        StatisticsResponseDTO stats;
        if (year != null && week != null) {
            stats = statisticsService.getWeeklyStatistics(email, year, week);
        } else {
            stats = statisticsService.getWeeklyStatistics(email);
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * /api/statistics/monthly?year=2025&month=5
     * si no mandas year+month devuelve el mes actual
     */
    @GetMapping("/monthly")
    public ResponseEntity<StatisticsResponseDTO> getMonthlyStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        String email = userDetails.getUsername();
        StatisticsResponseDTO stats;
        if (year != null && month != null) {
            stats = statisticsService.getMonthlyStatistics(email, year, month);
        } else {
            stats = statisticsService.getMonthlyStatistics(email);
        }
        return ResponseEntity.ok(stats);
    }
}
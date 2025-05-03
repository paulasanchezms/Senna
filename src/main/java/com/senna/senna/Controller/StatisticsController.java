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

    @GetMapping("/weekly")
    public ResponseEntity<StatisticsResponseDTO> getWeeklyStatistics(
            @AuthenticationPrincipal UserDetails userDetails) {
        StatisticsResponseDTO stats = statisticsService.getWeeklyStatistics(userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/monthly")
    public ResponseEntity<StatisticsResponseDTO> getMonthlyStatistics(
            @AuthenticationPrincipal UserDetails userDetails) {
        StatisticsResponseDTO stats = statisticsService.getMonthlyStatistics(userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }
}
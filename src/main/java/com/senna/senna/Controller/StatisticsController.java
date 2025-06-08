
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

    // Devuelve las estadísticas semanales del usuario autenticado o de un paciente concreto (si es psicólogo)
    @GetMapping("/weekly")
    public ResponseEntity<StatisticsResponseDTO> getWeeklyStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "week", required = false) Integer week,
            @RequestParam(value = "patientId", required = false) Long patientId
    ) {
        String email = userDetails.getUsername();
        StatisticsResponseDTO stats;

        if (year != null && week != null) {
            stats = statisticsService.getWeeklyStatistics(email, year, week, patientId);
        } else {
            stats = statisticsService.getWeeklyStatistics(email, patientId);
        }
        return ResponseEntity.ok(stats);
    }

    // Devuelve las estadísticas mensuales del usuario autenticado o de un paciente concreto (si es psicólogo)
    @GetMapping("/monthly")
    public ResponseEntity<StatisticsResponseDTO> getMonthlyStatistics(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "patientId", required = false) Long patientId
    ) {
        String email = userDetails.getUsername();
        StatisticsResponseDTO stats;

        if (year != null && month != null) {
            stats = statisticsService.getMonthlyStatistics(email, year, month, patientId);
        } else {
            stats = statisticsService.getMonthlyStatistics(email, patientId);
        }
        return ResponseEntity.ok(stats);
    }
}
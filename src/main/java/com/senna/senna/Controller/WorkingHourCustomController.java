package com.senna.senna.Controller;

import com.senna.senna.DTO.WorkingHourCustomDTO;
import com.senna.senna.Service.WorkingHourCustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/working-hours/custom")
@RequiredArgsConstructor
public class WorkingHourCustomController {

    private final WorkingHourCustomService customService;

    @GetMapping
    public List<WorkingHourCustomDTO> getForDate(@RequestParam(name = "userId") Long userId, @RequestParam String date) {
        return customService.getByDate(userId, date);
    }

    @PostMapping
    public void saveCustomHours(
            @RequestParam(name = "userId") Long userId,
            @RequestParam String date,
            @RequestBody List<WorkingHourCustomDTO> hours
    ) {
        customService.replaceByDate(userId, date, hours);
    }

}
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
    public List<WorkingHourCustomDTO> getForDate(@RequestParam Long profileId, @RequestParam String date) {
        return customService.getByDate(profileId, date);
    }

    @PostMapping
    public void saveCustomHours(
            @RequestParam Long profileId,
            @RequestParam String date,
            @RequestBody List<WorkingHourCustomDTO> hours
    ) {
        customService.replaceByDate(profileId, date, hours);
    }
}
package com.senna.senna.Controller;

import com.senna.senna.Entity.Mood;
import com.senna.senna.Service.MoodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    //Obtiene una lista de estados de ánimo
    @GetMapping
    public List<Mood> getAllMoods() {
        return moodService.getAllMoods();
    }
}
package com.senna.senna.Controller;

import com.senna.senna.Entity.Symptom;
import com.senna.senna.Service.SymptomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/symptoms")
public class SymptomController {

    private final SymptomService symptomService;

    public SymptomController(SymptomService symptomService) {
        this.symptomService = symptomService;
    }

    //Obtiene una lista de los síntomas
    @GetMapping
    public List<Symptom> getAllSymptoms() {
        return symptomService.getAllSymptoms();
    }
}
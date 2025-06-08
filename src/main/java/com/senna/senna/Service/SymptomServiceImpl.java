package com.senna.senna.Service;

import com.senna.senna.Entity.Symptom;
import com.senna.senna.Repository.SymptomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SymptomServiceImpl implements SymptomService {

    private final SymptomRepository symptomRepository;

    public SymptomServiceImpl(SymptomRepository symptomRepository) {
        this.symptomRepository = symptomRepository;
    }


    /**
     * Obtiene todos los síntomas registrados en la base de datos.
     * @return Lista de entidades Symptom.
     */
    @Override
    public List<Symptom> getAllSymptoms() {
        return symptomRepository.findAll();
    }
}
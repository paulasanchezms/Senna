package com.senna.senna.Config;

import com.senna.senna.Entity.Mood;
import com.senna.senna.Entity.Symptom;
import com.senna.senna.Repository.MoodRepository;
import com.senna.senna.Repository.SymptomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final MoodRepository moodRepository;
    private final SymptomRepository symptomRepository;

    public DataLoader(MoodRepository moodRepository, SymptomRepository symptomRepository) {
        this.moodRepository = moodRepository;
        this.symptomRepository = symptomRepository;
    }

    @Override
    public void run(String... args) {
        // Si no hay estados de ánimo en la base de datos, los inserta por defecto
        if (moodRepository.count() == 0) {
            moodRepository.save(new Mood(null, "Feliz", "happy.png"));
            moodRepository.save(new Mood(null, "Triste", "sad.png"));
            moodRepository.save(new Mood(null, "Ansioso", "anxious.png"));
            moodRepository.save(new Mood(null, "Motivado", "motivated.png"));
            moodRepository.save(new Mood(null, "Cansado", "tired.png"));
            moodRepository.save(new Mood(null, "Irritado", "irritated.png"));
            moodRepository.save(new Mood(null, "Relajado", "relaxed.png"));
            moodRepository.save(new Mood(null, "Estresado", "stressed.png"));
            moodRepository.save(new Mood(null, "Emocionado", "excited.png"));
            moodRepository.save(new Mood(null, "Preocupado", "worried.png"));
            moodRepository.save(new Mood(null, "Optimista", "optimistic.png"));
            moodRepository.save(new Mood(null, "Frustrado", "frustrated.png"));
        }

        // Si no hay síntomas en la base de datos, los inserta por defecto
        if (symptomRepository.count() == 0) {
            symptomRepository.save(new Symptom(null, "Dolor de cabeza", "headache.png"));
            symptomRepository.save(new Symptom(null, "Cansancio", "tiredness.png"));
            symptomRepository.save(new Symptom(null, "Insomnio", "insomnia.png"));
            symptomRepository.save(new Symptom(null, "Dolor muscular", "muscle-pain.png"));
            symptomRepository.save(new Symptom(null, "Náuseas", "nausea.png"));
            symptomRepository.save(new Symptom(null, "Falta de apetito", "loss-appetite.png"));
            symptomRepository.save(new Symptom(null, "Mareos", "dizziness.png"));
            symptomRepository.save(new Symptom(null, "Falta de concentración", "lack-focus.png"));
            symptomRepository.save(new Symptom(null, "Dolor de estómago", "stomachache.png"));
            symptomRepository.save(new Symptom(null, "Tensión muscular", "muscle-tension.png"));
            symptomRepository.save(new Symptom(null, "Palpitaciones", "palpitations.png"));
            symptomRepository.save(new Symptom(null, "Sudoración excesiva", "sweating.png"));
        }
    }
}
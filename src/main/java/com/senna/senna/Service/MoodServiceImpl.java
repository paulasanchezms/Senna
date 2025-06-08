package com.senna.senna.Service;

import com.senna.senna.Entity.Mood;
import com.senna.senna.Repository.MoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoodServiceImpl implements MoodService {

    private final MoodRepository moodRepository;

    public MoodServiceImpl(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    /**
     * Recupera todos los estados de ánimo disponibles en la base de datos.
     * @return lista de objetos Mood
     */
    @Override
    public List<Mood> getAllMoods() {
        return moodRepository.findAll();
    }
}
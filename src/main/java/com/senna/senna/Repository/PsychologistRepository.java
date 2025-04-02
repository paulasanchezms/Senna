package com.senna.senna.Repository;

import com.senna.senna.Entity.Psychologist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PsychologistRepository  extends JpaRepository<Psychologist, String> {
}

package com.senna.senna.Repository;

import com.senna.senna.Entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymptomRepository extends JpaRepository<Symptom, Long> {
}
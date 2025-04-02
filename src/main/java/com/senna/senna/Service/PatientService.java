package com.senna.senna.Service;

import com.senna.senna.Entity.Patient;
import com.senna.senna.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Crear un nuevo paciente
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Obtener todos los pacientes
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Obtener un paciente por ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Actualizar un paciente
    public Patient updatePatient(Long id, Patient updatedPatient) {
        return patientRepository.findById(id)
                .map(patient -> {
                    // Actualizar campos específicos del paciente
                    patient.getUser().setName(updatedPatient.getUser().getName());
                    patient.getUser().setLast_name(updatedPatient.getUser().getLast_name());
                    patient.getUser().setEmail(updatedPatient.getUser().getEmail());
                    patient.getUser().setPassword(updatedPatient.getUser().getPassword());
                    return patientRepository.save(patient);
                })
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));
    }

    // Eliminar un paciente
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
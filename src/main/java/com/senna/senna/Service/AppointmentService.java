package com.senna.senna.Service;

import com.senna.senna.DTO.AppointmentDTO;
import com.senna.senna.Entity.Appointment;
import java.util.List;

public interface AppointmentService {
    Appointment scheduleAppointment(String patientEmail, AppointmentDTO dto);
    Appointment updateAppointment(Long appointmentId, AppointmentDTO dto);
    void cancelAppointment(Long appointmentId);
    List<Appointment> getAppointmentsForPatient(String patientEmail);
    List<Appointment> getAppointmentsForPsychologist(String psychologistEmail);
}
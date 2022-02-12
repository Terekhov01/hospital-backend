package com.NetCracker.services;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.appointment.AppointmentRegistration;
import com.NetCracker.payload.Request.AppointmentCreationDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    List<Appointment> getAll();

    Appointment getById(Long theId);

    Optional<Appointment> findByAppointmentRegistration(AppointmentRegistration appointmentRegistration);

    void editAppointment(Appointment theAppointment);

    void delete(Long theId);

    Appointment createAppointment(AppointmentCreationDTO appointmentDTO, List<MultipartFile> filesToUpload, AppointmentRegistration appointmentRegistration) throws DataAccessException, IOException;
}

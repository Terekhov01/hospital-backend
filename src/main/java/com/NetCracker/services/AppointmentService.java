package com.NetCracker.services;

import com.NetCracker.entities.appointment.Appointment;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AppointmentService {

    List<Appointment> getAll();

    Appointment getById(Long theId);

    void editAppointment(Appointment theAppointment);

    void delete(Long theId);

}

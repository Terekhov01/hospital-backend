package com.NetCracker.Services;

import com.NetCracker.Entities.Appointment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AppointmentService {

    List<Appointment> getAll();

    Appointment getById(Long theId);

    void editAppointment(Appointment theAppointment);

    void delete(Long theId);

}

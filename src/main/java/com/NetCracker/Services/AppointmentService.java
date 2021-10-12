package com.NetCracker.Services;

import com.NetCracker.Entities.Appointment;

import java.util.List;

public interface AppointmentService {

    public List<Appointment> findAll();

    public Appointment findById(int theId);

    public void save(Appointment theAppointment);

    public void deleteById(int theId);

}

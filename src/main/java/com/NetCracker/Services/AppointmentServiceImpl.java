package com.NetCracker.Services;

import com.NetCracker.Entities.Appointment;
import com.NetCracker.Repositories.AppointmentRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepo appointmentRepo;

    public AppointmentServiceImpl() {
    }

    @Autowired
    public AppointmentServiceImpl(AppointmentRepo theAppointmentRepo) {
        appointmentRepo = theAppointmentRepo;
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepo.findAllByOrderByStartAsc();
    }

    @Override
    public Appointment findById(int theId) {
        Optional<Appointment> result = appointmentRepo.findById(theId);
        Appointment theAppointment;

        if (result.isPresent()) {
            theAppointment = result.get();
        } else {
            throw new RuntimeException("Did not find appointment id - " + theId);
        }

        return theAppointment;
    }

    @Override
    public void save(Appointment theAppointment) {
        appointmentRepo.save(theAppointment);
    }

    @Override
    public void deleteById(int theId) {
        appointmentRepo.deleteById(theId);
    }
}

package com.NetCracker.services;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public List<Appointment> getAll() {
        return appointmentRepo.findAllByOrderByIdAsc();
    }

    @Override
    @Transactional
    public Appointment getById(Long theId) {
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
    @Transactional
    public void editAppointment(Appointment theAppointment) {
        appointmentRepo.save(theAppointment);
    }

    @Override
    @Transactional
    public void delete(Long theId) {
        appointmentRepo.deleteById(theId);
    }
}

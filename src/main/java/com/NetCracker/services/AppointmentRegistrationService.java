package com.NetCracker.services;

import com.NetCracker.entities.appointment.AppointmentRegistration;
import com.NetCracker.repositories.appointment.AppointmentRegistrationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AppointmentRegistrationService
{
    @Autowired
    private AppointmentRegistrationRepo appointmentRegistrationRepo;

    @Transactional
    public Optional<AppointmentRegistration> findById(Long id)
    {
        return appointmentRegistrationRepo.findById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(AppointmentRegistration appointmentRegistration)
    {
        appointmentRegistrationRepo.delete(appointmentRegistration);
    }
}

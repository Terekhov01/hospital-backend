package com.NetCracker.services;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.services.doctor.DoctorUserService;
import com.NetCracker.services.doctor.DoctorUserServiceImpl;
import com.NetCracker.services.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService
{
    @Autowired
    PatientService patientService;

    @Autowired
    DoctorUserServiceImpl doctorUserService;

    public Doctor getAuthenticatedDoctor(Authentication authentication) throws DataAccessException,
                                                                                        ClassCastException
    {
        UserDetailsImpl userDetails = null;
        Doctor authenticatedDoctor = null;

        userDetails = (UserDetailsImpl) authentication.getPrincipal();
        authenticatedDoctor = doctorUserService.findByRelatedUserId(userDetails.getId());

        return authenticatedDoctor;
    }

    public Patient getAuthenticatedPatient(Authentication authentication) throws DataAccessException,
                                                                                        ClassCastException
    {
        UserDetailsImpl userDetails = null;
        Patient patient = null;

        userDetails = (UserDetailsImpl) authentication.getPrincipal();
        patient = patientService.findByRelatedUserId(userDetails.getId());

        return patient;
    }
}

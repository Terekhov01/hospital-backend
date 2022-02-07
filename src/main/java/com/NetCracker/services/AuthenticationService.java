package com.NetCracker.services;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.services.doctor.DoctorUserService;
import com.NetCracker.services.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;

public class AuthenticationService
{
    @Autowired
    static PatientService patientService;

    @Autowired
    static DoctorUserService doctorUserService;

    public static Doctor getAuthenticatedDoctor(Authentication authentication) throws DataAccessException, ClassCastException
    {
        UserDetailsImpl userDetails = null;
        Doctor authenticatedDoctor = null;

        userDetails = (UserDetailsImpl) authentication.getPrincipal();
        authenticatedDoctor = doctorUserService.findByRelatedUserId(userDetails.getId());

        return authenticatedDoctor;
    }

    public static Patient getAuthenticatedPatient(Authentication authentication) throws DataAccessException, ClassCastException
    {
        UserDetailsImpl userDetails = null;
        Patient patient = null;

        userDetails = (UserDetailsImpl) authentication.getPrincipal();
        patient = patientService.findByRelatedUserId(userDetails.getId());

        return patient;
    }
}

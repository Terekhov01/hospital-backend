package com.NetCracker.services.security;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.User;
import com.NetCracker.services.PatientService;
import com.NetCracker.services.doctor.DoctorUserServiceImpl;
import com.NetCracker.services.user.UserDetailsImpl;
import com.NetCracker.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService
{
    @Autowired
    private DoctorUserServiceImpl doctorUserService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

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

    public User getAuthenticatedUser(Authentication authentication) throws DataAccessException, ClassCastException
    {
        UserDetailsImpl userDetails = null;
        User user = null;

        userDetails = (UserDetailsImpl) authentication.getPrincipal();
        user = userService.findById(userDetails.getId());

        return user;
    }

    public User getAuthenticatedAdmin(Authentication authentication) throws DataAccessException, ClassCastException
    {
        UserDetailsImpl userDetails = null;
        User user = null;

        userDetails = (UserDetailsImpl) authentication.getPrincipal();
        user = userService.findById(userDetails.getId());

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN)))
        {
            return user;
        }

        return null;
    }
}

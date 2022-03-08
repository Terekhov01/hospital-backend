package com.NetCracker.services.security;

import com.NetCracker.entities.ConfirmationToken;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.entities.schedule.DoctorSchedule;
import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.role.Role;
import com.NetCracker.entities.user.User;
import com.NetCracker.payload.Request.DoctorSignupRequest;
import com.NetCracker.payload.Request.PatientSignupRequest;
import com.NetCracker.payload.Request.UserSignupRequest;
import com.NetCracker.payload.Response.MessageResponse;
import com.NetCracker.repositories.ConfirmationTokenRepository;
import com.NetCracker.repositories.RoleRepository;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.services.PatientService;
import com.NetCracker.services.doctor.DoctorUserServiceImpl;
import com.NetCracker.services.doctor.RoomService;
import com.NetCracker.services.doctor.SpecialistService;
import com.NetCracker.services.schedule.ScheduleService;
import com.NetCracker.services.user.UserDetailsImpl;
import com.NetCracker.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService
{
    @Autowired
    private DoctorUserServiceImpl doctorUserService;

    @Autowired
    private PatientService patientService;

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

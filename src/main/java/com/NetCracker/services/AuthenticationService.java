package com.NetCracker.services;

import com.NetCracker.entities.ConfirmationToken;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.entities.schedule.DoctorSchedule;
import com.NetCracker.entities.user.ERole;
import com.NetCracker.entities.user.Role;
import com.NetCracker.entities.user.User;
import com.NetCracker.payload.Request.SignupRequest;
import com.NetCracker.repositories.ConfirmationTokenRepository;
import com.NetCracker.repositories.RoleRepository;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.services.doctor.DoctorUserServiceImpl;
import com.NetCracker.services.schedule.ScheduleService;
import com.NetCracker.services.user.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    private PatientService patientService;

    @Autowired
    private DoctorUserServiceImpl doctorUserService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

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

    @AllArgsConstructor
    @Getter
    public static class UserRegistrationData
    {
        User user;
        ConfirmationToken confirmationToken;
    }

    @Transactional
    public UserRegistrationData registerUser(SignupRequest signUpRequest) throws DataAccessException
    {
        // Create new user's account
        User user = new User(signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getPatronymic(),
                signUpRequest.getPhone(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role ->
            {
                switch (role)
                {
                    case "admin" ->
                    {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "mod" ->
                    {
                        Role modRole = roleRepository.findByName(ERole.ROLE_DOCTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    default ->
                    {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_DOCTOR)))
        {
            var doctor = new Doctor();
            doctorUserService.save(doctor);
            var doctorSchedule = new DoctorSchedule(doctor);
            scheduleService.save(doctorSchedule);
            doctor.setSchedule(doctorSchedule);
        }

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_PATIENT)))
        {
            Patient patient = new Patient(signUpRequest.getPassport(),
                    signUpRequest.getPolys());
            patient.setUser(user);
            patientRepository.save(patient);
        }

        var confirmationToken = createToken(user);

        return new UserRegistrationData(user, confirmationToken);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ConfirmationToken createToken(User user)
    {
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        return confirmationToken;
    }
}

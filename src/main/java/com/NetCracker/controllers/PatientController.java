package com.NetCracker.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.entities.patient.Patient;
import com.NetCracker.entities.user.User;
import com.NetCracker.exceptions.PatientNotFoundException;
import com.NetCracker.payload.Request.PatientSignupRequest;
import com.NetCracker.payload.Request.PatientDTO;
import com.NetCracker.payload.Response.PatientPersinalAccountDTO;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.services.PatientService;
import com.NetCracker.services.security.AuthenticationService;
import com.NetCracker.services.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    PatientRepository repository;

    @Autowired
    PatientService patientService;

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ObjectMapper objectMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients(@RequestParam(required = false) Long id) {
        try {
            List<Patient> patients = new ArrayList<>();

            if (id == null)
                patients.addAll(repository.findAllByOrderByIdAsc());
            else
                patients.add(repository.findById(id).orElseThrow(() -> new PatientNotFoundException(id)));

            if (patients.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(patients, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/count-all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> countAll()
    {
        Long patientAmount = null;
        try
        {
            patientAmount = patientService.countAll();
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Не удалось получить количество пациентов больницы " +
                                                        "- база данных недоступна", HttpStatus.SERVICE_UNAVAILABLE);
        }

        return new ResponseEntity<String>(patientAmount.toString(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Patient> getPatientAccountInfoById(@PathVariable("id") Long id) {
        Optional<Patient> patientData = repository.findById(id);

        return patientData.map(patient ->
                new ResponseEntity<>(patient, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/lastname/{lastName}")
    public ResponseEntity<Patient> getDoctorByLastName(@PathVariable("lastName") String lastName) {
        Optional<Patient> patientData = repository.findPatientByLastName(lastName);

        return patientData.map(doctor ->
                new ResponseEntity<>(doctor, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        try {
            Patient _patient = repository
                    .save(new Patient(patient.getUser(), patient.getPassport(), patient.getPolys()/*, patient.getFiles()*/));
            return new ResponseEntity<>(_patient, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") Long id, @RequestBody PatientSignupRequest patientSignupRequest) {
        Optional<Patient> patientData = repository.findById(id);
        User user =  userService.findById(id);

        user.setFirstName(patientSignupRequest.getFirstName());
        user.setLastName(patientSignupRequest.getLastName());
        user.setPatronymic(patientSignupRequest.getMiddleName());
        user.setPhone(patientSignupRequest.getPhone());

        if (patientData.isPresent()) {
            Patient _patient = patientData.get();
            _patient.setPassport(patientSignupRequest.getPassport());
            _patient.setPolys(patientSignupRequest.getPolys());
            _patient.setUser(user);
            userService.saveUser(user);
//            _patient.setFiles(patient.getFiles());
            return new ResponseEntity<>(repository.save(_patient), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteAllPatients() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<String> getPatientAccountInfoById(@PathVariable Long id, Authentication authentication)
    {
        Patient authenticatedPatient = null;
        try
        {
            authenticatedPatient = authenticationService.getAuthenticatedPatient(authentication);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("Аутентификация не пройдена", HttpStatus.UNAUTHORIZED);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Аутентификация не пройдена. Ошибка сервера связаться с БД",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (authenticatedPatient == null)
        {
            return new ResponseEntity<String>("Не найден пациент с Вашей регистрационной информацией. " +
                    "Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        var patientPersonalAccountDTO = new PatientPersinalAccountDTO(authenticatedPatient);
        String patientPersonalAccountStr;
        try
        {
            patientPersonalAccountStr = objectMapper.writeValueAsString(patientPersonalAccountDTO);
        }
        catch (JsonProcessingException e)
        {
            return ResponseEntity.internalServerError().body("Ошибка на сервере - не удалось преобразовать " +
                    "информацию в строку");
        }

        return ResponseEntity.ok().body(patientPersonalAccountStr);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<?> updatePatient(@RequestBody PatientDTO patientDTO, Authentication authentication)
    {
        Patient authenticatedPatient;
        try
        {
            authenticatedPatient = authenticationService.getAuthenticatedPatient(authentication);
        }
        catch (ClassCastException e)
        {
            return ResponseEntity.internalServerError().body("Не удалось подтвердить Ваш аккаунт");
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Не удалось связаться с базой данных для подтверждения аккаунта",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (authenticatedPatient == null)
        {
            return ResponseEntity.internalServerError().body("Не удалось подтвердить Ваш аккаунт");
        }

        Patient patient = patientService.findById(authenticatedPatient.getId());

        patient.getUser().setFirstName(patientDTO.getFirstName());
        patient.getUser().setLastName(patientDTO.getLastName());
        patient.getUser().setPatronymic(patientDTO.getMiddleName());
        patient.getUser().setPhone(patientDTO.getPhone());
        patient.setPassport(patientDTO.getPassport());
        patient.setPolys(patientDTO.getPolys());
        patientService.savePatient(patient);

        return ResponseEntity.ok(null);
    }
}

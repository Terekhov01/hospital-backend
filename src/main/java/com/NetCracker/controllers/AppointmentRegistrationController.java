package com.NetCracker.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.entities.appointment.AppointmentRegistration;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.exceptions.AppointmentRegistrationNotFoundException;
import com.NetCracker.repositories.appointment.AppointmentRegistrationRepo;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.repositories.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class AppointmentRegistrationController {
    @Autowired
    AppointmentRegistrationRepo repository;

    @Autowired
    DoctorRepository doctors;

    @Autowired
    PatientRepository patients;

    @GetMapping("/appointmentRegistrations")
    public ResponseEntity<List<AppointmentRegistration>> getAllAppointmentRegistrations(@RequestParam(required = false) Long id) {
        try {
            List<AppointmentRegistration> appointmentRegistrations = new ArrayList<>();

            if (id == null)
                appointmentRegistrations.addAll(repository.findAllByOrderByIdAsc());
            else
                appointmentRegistrations.add(repository.findById(id).orElseThrow(() -> new AppointmentRegistrationNotFoundException(id)));

            if (appointmentRegistrations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(appointmentRegistrations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appointmentRegistrations/{doc}/{pat}")
    public ResponseEntity<AppointmentRegistration> getAppointmentRegistrationByDoctorAndPatient(@PathVariable("doc") String doc, @PathVariable("pat") String pat) {
        Optional<AppointmentRegistration> appointmentRegistrationData = repository.findByDoctorAndPatient(doc, pat);
        return appointmentRegistrationData.map(appointmentRegistration ->
                new ResponseEntity<>(appointmentRegistration, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/appointmentRegistrations/{id}")
    public ResponseEntity<AppointmentRegistration> getAppointmentRegistrationById(@PathVariable("id") Long id) {
        Optional<AppointmentRegistration> appointmentRegistrationData = repository.findById(id);
        return appointmentRegistrationData.map(appointmentRegistration ->
                new ResponseEntity<>(appointmentRegistration, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/appointmentRegistrations/patient/{id}")
    public ResponseEntity<List<AppointmentRegistration>> getPatientAppointmentRegistration(@PathVariable("id") Long id) {
        try {
            List<AppointmentRegistration> appointmentRegistrations = new ArrayList<>(repository.findAllByPatientNotConducted(id));
            if (appointmentRegistrations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointmentRegistrations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appointmentRegistrations/doctor/{id}")
    public ResponseEntity<List<AppointmentRegistration>> getDoctorAppointmentRegistration(@PathVariable("id") Long id) {
        try {
            List<AppointmentRegistration> appointmentRegistrations = new ArrayList<>(repository.findAllByDoctorNotConducted(id));
            if (appointmentRegistrations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointmentRegistrations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/appointmentRegistrations")
    public ResponseEntity<AppointmentRegistration> createAppointmentRegistration(@RequestBody AppointmentRegistration appointmentRegistration) {

        Optional<Doctor> doctor = doctors.findById(appointmentRegistration.getDoctor().getId());
        Optional<Patient> patient = patients.findById(appointmentRegistration.getPatient().getUser().getId());
        if (doctor.isPresent() && patient.isPresent()) {
            try {
                AppointmentRegistration _appointmentRegistration = repository
                        .save(new AppointmentRegistration(appointmentRegistration.getService(), appointmentRegistration.getStart(),
                                appointmentRegistration.getEnd(), appointmentRegistration.getAddress(), appointmentRegistration.getRoom(),
                                patient.get(), doctor.get()));
                return new ResponseEntity<>(_appointmentRegistration, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/appointmentRegistrations/{id}")
    public ResponseEntity<AppointmentRegistration> updateAppointmentRegistration(@PathVariable("id") Long id, @RequestBody AppointmentRegistration appointmentRegistration) {
        Optional<AppointmentRegistration> appointmentRegistrationData = repository.findById(id);
        Optional<Doctor> doctor = doctors.findDoctorByLastName(appointmentRegistration.getDoctor().getUser().getLastName());
        Optional<Patient> patient = patients.findPatientByLastName(appointmentRegistration.getPatient().getUser().getLastName());
        System.out.println("In updating ar");
        System.out.println("Doc: " + appointmentRegistration.getDoctor().getUser().getLastName());
        System.out.println("Pat: " + appointmentRegistration.getPatient().getUser().getLastName());
        System.out.println("Is present? " + appointmentRegistrationData.isPresent());
        if (doctor.isPresent() && patient.isPresent()) {
            if (appointmentRegistrationData.isPresent()) {
                AppointmentRegistration _appointmentRegistration = appointmentRegistrationData.get();
                _appointmentRegistration.setStart(appointmentRegistration.getStart());
                _appointmentRegistration.setEnd(appointmentRegistration.getEnd());
                _appointmentRegistration.setService(appointmentRegistration.getService());
                _appointmentRegistration.setAddress(appointmentRegistration.getAddress());
                _appointmentRegistration.setRoom(appointmentRegistration.getRoom());
                _appointmentRegistration.setPatient(patient.get());
                _appointmentRegistration.setDoctor(doctor.get());
                return new ResponseEntity<>(repository.save(_appointmentRegistration), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/appointmentRegistrations/{id}")
    public ResponseEntity<HttpStatus> deleteAppointmentRegistration(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/appointmentRegistrations")
    public ResponseEntity<HttpStatus> deleteAllAppointmentRegistrations() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

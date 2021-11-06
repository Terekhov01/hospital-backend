package com.NetCracker.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.Entities.AppointmentRegistration;
import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Patient;
import com.NetCracker.Exceptions.AppointmentRegistrationNotFoundException;
import com.NetCracker.Repositories.AppointmentRegistrationRepo;
import com.NetCracker.Repositories.DoctorRepository;
import com.NetCracker.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class AppointmentRegistrationController {
    @Autowired
    AppointmentRegistrationRepo repository;

    @Autowired
    DoctorRepository doctors;

    @Autowired
    PatientRepository patients;

    @GetMapping("/appointmentRegistrations")
    public ResponseEntity<List<AppointmentRegistration>> getAllAppointmentRegistrations(@RequestParam(required = false) Integer id) {
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
    public ResponseEntity<AppointmentRegistration> getAppointmentRegistrationById(@PathVariable("id") int id) {
        Optional<AppointmentRegistration> appointmentRegistrationData = repository.findById(id);
        return appointmentRegistrationData.map(appointmentRegistration ->
                new ResponseEntity<>(appointmentRegistration, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/appointmentRegistrations")
    public ResponseEntity<AppointmentRegistration> createAppointmentRegistration(@RequestBody AppointmentRegistration appointmentRegistration) {

        Optional<Doctor> doctor = doctors.findDoctorByLastName(appointmentRegistration.getDoctor().getLastName());
        Optional<Patient> patient = patients.findPatientByLastName(appointmentRegistration.getPatient().getLastName());
        if (doctor.isPresent() && patient.isPresent()) {
            try {
                AppointmentRegistration _appointmentRegistration = repository
                        .save(new AppointmentRegistration(appointmentRegistration.getStart(),
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
    public ResponseEntity<AppointmentRegistration> updateAppointmentRegistration(@PathVariable("id") int id, @RequestBody AppointmentRegistration appointmentRegistration) {
        Optional<AppointmentRegistration> appointmentRegistrationData = repository.findById(id);
        Optional<Doctor> doctor = doctors.findDoctorByLastName(appointmentRegistration.getDoctor().getLastName());
        Optional<Patient> patient = patients.findPatientByLastName(appointmentRegistration.getPatient().getLastName());
        System.out.println("In updating ar");
        System.out.println("Doc: " + appointmentRegistration.getDoctor().getLastName());
        System.out.println("Pat: " + appointmentRegistration.getPatient().getLastName());
        System.out.println("Is present? " + appointmentRegistrationData.isPresent());
        if (doctor.isPresent() && patient.isPresent()) {
            if (appointmentRegistrationData.isPresent()) {
                AppointmentRegistration _appointmentRegistration = appointmentRegistrationData.get();
                _appointmentRegistration.setStart(appointmentRegistration.getStart());
                _appointmentRegistration.setEnd(appointmentRegistration.getEnd());
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
    public ResponseEntity<HttpStatus> deleteAppointmentRegistration(@PathVariable("id") int id) {
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

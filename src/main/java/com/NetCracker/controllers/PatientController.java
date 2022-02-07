package com.NetCracker.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.entities.patient.Patient;
import com.NetCracker.exceptions.PatientNotFoundException;
import com.NetCracker.repositories.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class PatientController {

    @Autowired
    PatientRepository repository;

    @GetMapping("/patients")
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

    @GetMapping("/patients/id/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") Long id) {
        Optional<Patient> patientData = repository.findById(id);

        return patientData.map(patient ->
                new ResponseEntity<>(patient, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/patients/lastname/{lastName}")
    public ResponseEntity<Patient> getDoctorByLastName(@PathVariable("lastName") String lastName) {
        Optional<Patient> patientData = repository.findPatientByLastName(lastName);

        return patientData.map(doctor ->
                new ResponseEntity<>(doctor, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/patients")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        try {
            Patient _patient = repository
                    .save(new Patient(patient.getUser(), patient.getPassport(), patient.getPolys()/*, patient.getFiles()*/));
            return new ResponseEntity<>(_patient, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/patients/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("id") Long id, @RequestBody Patient patient) {
        Optional<Patient> patientData = repository.findById(id);

        if (patientData.isPresent()) {
            Patient _patient = patientData.get();
            _patient.setUser(patient.getUser());
            _patient.setPassport(patient.getPassport());
            _patient.setPolys(patient.getPolys());
//            _patient.setFiles(patient.getFiles());
            return new ResponseEntity<>(repository.save(_patient), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/patients")
    public ResponseEntity<HttpStatus> deleteAllPatients() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

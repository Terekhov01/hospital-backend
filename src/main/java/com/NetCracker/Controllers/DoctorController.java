package com.NetCracker.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Exceptions.DoctorNotFoundException;
import com.NetCracker.Repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class DoctorController {

    @Autowired
    DoctorRepository repository;

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors(@RequestParam(required = false) Integer id) {
        try {
            List<Doctor> doctors = new ArrayList<>();

            if (id == null)
                doctors.addAll(repository.findAllByOrderByIdAsc());
            else
                doctors.add(repository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id)));

            if (doctors.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(doctors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/doctors/id/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable("id") int id) {
        Optional<Doctor> doctorData = repository.findById(id);

        return doctorData.map(doctor ->
                new ResponseEntity<>(doctor, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/doctors/lastname/{lastName}")
    public ResponseEntity<Doctor> getDoctorByLastName(@PathVariable("lastName") String lastName) {
        Optional<Doctor> doctorData = repository.findDoctorByLastName(lastName);

        return doctorData.map(doctor ->
                new ResponseEntity<>(doctor, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/doctors")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        try {
            Doctor _doctor = repository
                    .save(new Doctor(doctor.getLastName()));
            return new ResponseEntity<>(_doctor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable("id") int id, @RequestBody Doctor doctor) {
        Optional<Doctor> doctorData = repository.findById(id);

        if (doctorData.isPresent()) {
            Doctor _doctor = doctorData.get();
            _doctor.setLastName(doctor.getLastName());
            return new ResponseEntity<>(repository.save(_doctor), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable("id") int id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/doctors")
    public ResponseEntity<HttpStatus> deleteAllDoctors() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.NetCracker.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import com.NetCracker.Entities.Doctor;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.exceptions.DoctorNotFoundException;
import com.NetCracker.repositories.doctor.DoctorRepository;
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
    public ResponseEntity<List<Doctor>> getAllDoctors(@RequestParam(required = false) Long id) {
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
    public ResponseEntity<Doctor> getDoctorById(@PathVariable("id") Long id) {
        Optional<Doctor> doctorData = repository.findById(id);

        return doctorData.map(doctor ->
                new ResponseEntity<>(doctor, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/doctors/lastname/{lastName}")
    public ResponseEntity<Doctor> getDoctorByLastName(@PathVariable("lastName") String lastName) {
        System.out.println("In get method int Doctor");
        System.out.println("Last name: " + lastName);
        Optional<Doctor> doctorData = repository.findDoctorByLastName(lastName);

        if (doctorData.isPresent()) {
            System.out.println("Found Doctor!");
        } else {
            System.out.println("Doctor Not Found!");
        }

        return doctorData.map(doctor ->
                new ResponseEntity<>(doctor, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/doctors")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        try {
            Doctor _doctor = repository
                    .save(new Doctor(doctor.getDateOfEmployment(), doctor.getEducation(), doctor.getRoom(), doctor.getSpecialist(), doctor.getSchedule(), doctor.getUser().getFirstName(), doctor.getUser().getLastName(), doctor.getRatings()));
            return new ResponseEntity<>(_doctor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable("id") Long id, @RequestBody Doctor doctor) {
        Optional<Doctor> doctorData = repository.findById(id);

        if (doctorData.isPresent()) {
            Doctor _doctor = doctorData.get();
            _doctor.setDateOfEmployment(doctor.getDateOfEmployment());
            _doctor.setEducation(doctor.getEducation());
            _doctor.setSpecialist(doctor.getSpecialist());
            _doctor.setSchedule(doctor.getSchedule());
            _doctor.getUser().setFirstName(doctor.getUser().getFirstName());
            _doctor.getUser().setLastName(doctor.getUser().getLastName());
            _doctor.setRoom(doctor.getRoom());
            _doctor.setRatings(doctor.getRatings());
            return new ResponseEntity<>(repository.save(_doctor), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable("id") Long id) {
        System.out.println(0);
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/doctors")
    public ResponseEntity<HttpStatus> deleteAllDoctors() {
        System.out.println(0);
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

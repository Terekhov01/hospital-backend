package com.NetCracker.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.Entities.Appointment;
import com.NetCracker.Entities.AppointmentRegistration;
import com.NetCracker.Exceptions.AppointmentNotFoundException;
import com.NetCracker.Repositories.AppointmentRegistrationRepo;
import com.NetCracker.Repositories.AppointmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
class AppointmentController {

    @Autowired
    AppointmentRepo repository;

    @Autowired
    AppointmentRegistrationRepo appointmentRegistrations;

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments(@RequestParam(required = false) Integer id) {
        try {
            List<Appointment> appointments = new ArrayList<>();

            if (id == null)
                appointments.addAll(repository.findAllByOrderByIdAsc());
            else
                appointments.add(repository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id)));

            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") int id) {
        Optional<Appointment> appointmentData = repository.findById(id);

        return appointmentData.map(appointment ->
                new ResponseEntity<>(appointment, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {

        Optional<AppointmentRegistration> appointmentRegistration =
                appointmentRegistrations.findByDoctorAndPatient(
                        appointment.getDoctor().getLastName(),
                        appointment.getPatient().getLastName());
        if (appointmentRegistration.isPresent()) {
            try {
                Appointment _appointment = repository
                        .save(new Appointment(appointment.getId(), appointmentRegistration.get(),
                                appointmentRegistration.get().getPatient(), appointmentRegistration.get().getDoctor(), appointment.getDescription(),
                                appointment.getService(), appointment.getRecipe(), appointment.getTreatPlan(),
                                appointment.getRehabPlan(), appointment.getDocStatement()));
                return new ResponseEntity<>(_appointment, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable("id") int id, @RequestBody Appointment appointment) {
        Optional<Appointment> appointmentData = repository.findById(id);
        Optional<AppointmentRegistration> appointmentRegistrationData = appointmentRegistrations.
                findByDoctorAndPatient(appointment.getAppointmentRegistration().getDoctor().getLastName(), appointment.getAppointmentRegistration().getPatient().getLastName());
        System.out.println("In updating a");
        System.out.println("Doc: " + appointmentRegistrationData.get().getDoctor().getLastName());
        System.out.println("Pat: " + appointmentRegistrationData.get().getPatient().getLastName());
        System.out.println("Is present? " + appointmentRegistrationData.isPresent());
        if (appointmentRegistrationData.isPresent()) {
            if (appointmentData.isPresent()) {
                Appointment _appointment = appointmentData.get();
                _appointment.setDescription(appointment.getDescription());
                _appointment.setDocStatement(appointment.getDocStatement());
                _appointment.setRecipe(appointment.getRecipe());
                _appointment.setRehabPlan(appointment.getRehabPlan());
                _appointment.setService(appointment.getService());
                _appointment.setTreatPlan(appointment.getTreatPlan());
                _appointment.setDoctor(appointmentRegistrationData.get().getDoctor());
                _appointment.setPatient(appointmentRegistrationData.get().getPatient());
                _appointment.setAppointmentRegistration(appointmentRegistrationData.get());
                return new ResponseEntity<>(repository.save(_appointment), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable("id") int id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/appointments")
    public ResponseEntity<HttpStatus> deleteAllAppointments() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
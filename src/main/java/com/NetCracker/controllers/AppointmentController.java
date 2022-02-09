package com.NetCracker.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.appointment.AppointmentRegistration;
import com.NetCracker.exceptions.AppointmentNotFoundException;
import com.NetCracker.repositories.appointment.AppointmentRegistrationRepo;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import com.NetCracker.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
class AppointmentController {

    @Autowired
    AppointmentRepo repository;

    @Autowired
    AppointmentRegistrationRepo appointmentRegistrations;

    @Autowired
    private EmailSenderService emailSenderService;
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments(@RequestParam(required = false) Long id) {
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

    @GetMapping("/appointments/last/{id}")
    public ResponseEntity<Appointment> getLastAppointment(@PathVariable("id") Long id) {
        Optional<Appointment> appointmentData = repository.findLastAppointment(id);

        return appointmentData.map(appointment ->
                new ResponseEntity<>(appointment, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") Long id) {
        Optional<Appointment> appointmentData = repository.findById(id);

        return appointmentData.map(appointment ->
                new ResponseEntity<>(appointment, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {

        Optional<AppointmentRegistration> appointmentRegistration =
                appointmentRegistrations.findByDoctorAndPatient(
                        appointment.getAppointmentRegistration().getDoctor().getUser().getLastName(),
                        appointment.getAppointmentRegistration().getPatient().getUser().getLastName());
//                        appointment.getDoctor().getLastName(),
//                        appointment.getPatient().getLastName());
//        System.out.println("Here1");
        if (appointmentRegistration.isPresent()) {
//            System.out.println("Here2");
            try {
//                System.out.println("Here3");
//                System.out.println("File is" + (appointment.getFile() == null));
                Appointment _appointment = repository
                        .save(new Appointment(appointment.getId(), appointmentRegistration.get(),
//                                appointmentRegistration.get().getPatient(), appointmentRegistration.get().getDoctor(),
                                appointment.getDescription(),
//                                appointment.getFile(),
//                                appointment.getService(),
                                appointment.getRecipe(), appointment.getTreatPlan(),
                                appointment.getRehabPlan(), appointment.getDocStatement(), appointment.getFiles()/*, appointment.getFile()*/));
//                System.out.println("Here4");
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(appointment.getAppointmentRegistration().getPatient().getUser().getEmail());
                mailMessage.setSubject("Запись на прием!");
                mailMessage.setFrom("netclinictech@mail.ru");
                mailMessage.setText("Вы успешно записались на прием к врачу"+appointment.getAppointmentRegistration().getDoctor().getUser().getLastName() +appointment.getAppointmentRegistration().getDoctor().getUser().getFirstName() + appointment.getFiles());

                emailSenderService.sendEmail(mailMessage);
                return new ResponseEntity<>(_appointment, HttpStatus.CREATED);
            } catch (Exception e) {
//                System.out.println("Here5");
//                System.out.println("Exception: " + e.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable("id") Long id, @RequestBody Appointment appointment) {
        Optional<Appointment> appointmentData = repository.findById(id);
        Optional<AppointmentRegistration> appointmentRegistrationData = appointmentRegistrations.
                findByDoctorAndPatient(appointment.getAppointmentRegistration().getDoctor().getUser().getLastName(), appointment.getAppointmentRegistration().getPatient().getUser().getLastName());
//        System.out.println("In updating a");
//        System.out.println("Doc: " + appointmentRegistrationData.get().getDoctor().getLastName());
//        System.out.println("Pat: " + appointmentRegistrationData.get().getPatient().getLastName());
//        System.out.println("Is present? " + appointmentRegistrationData.isPresent());
        if (appointmentRegistrationData.isPresent()) {
            if (appointmentData.isPresent()) {
                Appointment _appointment = appointmentData.get();
                _appointment.setDescription(appointment.getDescription());
                _appointment.setDocStatement(appointment.getDocStatement());
                _appointment.setRecipe(appointment.getRecipe());
                _appointment.setRehabPlan(appointment.getRehabPlan());
//                _appointment.setService(appointment.getService());
                _appointment.setTreatPlan(appointment.getTreatPlan());
//                _appointment.setFile(appointment.getFile());
//                _appointment.setDoctor(appointmentRegistrationData.get().getDoctor());
//                _appointment.setPatient(appointmentRegistrationData.get().getPatient());
                _appointment.setAppointmentRegistration(appointmentRegistrationData.get());
                _appointment.setFiles(appointment.getFiles());


                return new ResponseEntity<>(repository.save(_appointment), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable("id") Long id) {
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
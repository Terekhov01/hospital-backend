package com.NetCracker.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.appointment.AppointmentRegistration;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.exceptions.AppointmentNotFoundException;
import com.NetCracker.payload.Request.AppointmentCreationDTO;
import com.NetCracker.repositories.appointment.AppointmentRegistrationRepo;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import com.NetCracker.services.EmailSenderService;
import com.NetCracker.services.AppointmentRegistrationService;
import com.NetCracker.services.AppointmentService;
import com.NetCracker.services.security.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static java.util.stream.Collectors.toList;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
class AppointmentController {

    @Autowired
    AppointmentRepo repository;

    @Autowired
    AppointmentRegistrationRepo appointmentRegistrations;

    @Autowired
    AppointmentRegistrationService appointmentRegistrationService;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ObjectMapper objectMapper;

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

    @GetMapping("/appointments/doctor/{id}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable("id") Long id) {
        try {
            List<Appointment> appointments = new ArrayList<>(repository.findAllByDoctor(id));
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appointments/patient/{id}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable("id") Long id) {
        try {
            List<Appointment> appointments = new ArrayList<>(repository.findAllByPatient(id));
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

    @GetMapping("/appointments/doctorPaged/{id}")
    public Page<Appointment> list(@RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "2") int size,
                                  @RequestParam(name = "keyword", defaultValue = "") String keyWord,
                                  @PathVariable("id") Long id) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Pageable paging = PageRequest.of(page, size);
        Page<Appointment> pageResult;
        if (keyWord != null && !keyWord.equals("")) {
            pageResult = repository.findAllByDoctorAndKeyWordPaged(keyWord, paging, id);
        } else {
            pageResult = repository.findAllByDoctorPaged(paging, id);
        }
        List<Appointment> appointmentList = pageResult
                .stream()
                .map(Appointment::new)
                .collect(toList());
        return new PageImpl<>(appointmentList, pageRequest, pageResult.getTotalElements());

    }

    // Asserting file size is done via spring.
    // See spring.servlet.multipart.max-file-size in .properties
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping("/appointments")
    public ResponseEntity<String> createAppointment(@RequestParam MultipartFile appointmentDTOBlob,
                                                    @RequestParam(required = false) List<MultipartFile> filesToUpload,
                                                    Authentication authentication) {
        String appointmentDTOJson;
        System.out.println("In post mapping");
        try
        {
            appointmentDTOJson = new String(appointmentDTOBlob.getBytes(), StandardCharsets.UTF_8);
            System.out.println("appointmentDTOJson:\n");
            System.out.println(appointmentDTOJson);
        }
        catch (IOException e)
        {
            return new ResponseEntity<String>("???????????? ?????????????? ???????????????????? ???? ??????????????. ???????????????????????? ?????????????????? " +
                    "???????????? ?? ??????????????", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        AppointmentCreationDTO appointmentDTO;
        try
        {
            appointmentDTO = objectMapper.readValue(appointmentDTOJson, AppointmentCreationDTO.class);
        }
        catch (JsonProcessingException e)
        {
            return new ResponseEntity<String>("???????????? ?????????????? ???????????????????? ???? ??????????????. " +
                    "JsonProcessingException", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (appointmentDTO.isSickListNeeded() &&
                (appointmentDTO.getRecoveryDate() == null || appointmentDTO.getRecoveryDate().isBefore(LocalDate.now())))
        {
            return new ResponseEntity<String>("???????????????????????? ???????? ???????????? ???? ????????????", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        AppointmentRegistration appointmentRegistration;

        try
        {
            Optional<AppointmentRegistration> appointmentRegistrationOpt;
            try
            {
                appointmentRegistrationOpt = appointmentRegistrationService.findById(
                        appointmentDTO.getAppointmentRegistrationId());
            }
            catch (DataAccessException e)
            {
                return new ResponseEntity<String>("???????????? ?????????????????? ???????????????????????? ???????????? ???? ???????? ????????????. " +
                        "????????????????, ???????? ???????????? ?????????????? ??????????????????????. ???????????????????? ???????????????????????????????? ???????????????? ????????????",
                                                                                    HttpStatus.SERVICE_UNAVAILABLE);
            }

            if (appointmentRegistrationOpt.isEmpty())
            {
                return new ResponseEntity<String>("???? ?????????????? ???????????????? ?????????????????????????????? ????????????????????",
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }
            appointmentRegistration = appointmentRegistrationOpt.get();
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("?????? ?????????????????????? ?? ???????? ????????????", HttpStatus.SERVICE_UNAVAILABLE);
        }

        Doctor requestingDoctor;
        try
        {
            requestingDoctor = authenticationService.getAuthenticatedDoctor(authentication);

            if (!requestingDoctor.getId().equals(appointmentRegistration.getDoctor().getId()))
            {
                return new ResponseEntity<String>("?? ?????? ?????? ???????? ?????????????????? ?????? ??????????????", HttpStatus.UNAUTHORIZED);
            }
        }
        catch (ClassCastException ex)
        {
            return new ResponseEntity<String>("?????????? ???? ???????? ???????????????? ???????????????????? ?? ??????",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DataAccessException ex)
        {
            return new ResponseEntity<String>("?????????? ???? ???????? ???????????????? ???????????????????? ?? ???????????????????????? ???? ???????? ????????????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Check if an appointment related to requested appointmentRegistration was already conducted
        var conductedAppointment = appointmentService.findByAppointmentRegistration(appointmentRegistration);
        if (conductedAppointment.isPresent())
        {
            var appointmentDate = conductedAppointment.get().getAppointmentRegistration().getEnd();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy '??' HH:mm:ss");
            return new ResponseEntity<String>("???????? ?????????? ?????? ???????????????????? " + formatter.format(appointmentDate),
                                                                                        HttpStatus.SERVICE_UNAVAILABLE);
        }

        Appointment appointment;

        try
        {
            appointment = appointmentService.createAppointment(appointmentDTO, filesToUpload, appointmentRegistration);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????? ?????????? ?? ?????????? ????????????. ???????????????????? ???? ???????? ??????????????????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        catch (IOException ex)
        {
            return new ResponseEntity<String>("???????????? ???????????????????????? ?????????????????????? ??????????",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO - extract to a separate mailService
        try
        {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(appointment.getAppointmentRegistration().getPatient().getUser().getEmail());
            mailMessage.setSubject("???????????? ???? ??????????!");
            mailMessage.setFrom("netclinictech@mail.ru");
            mailMessage.setText("???? ?????????????? ???????????????????? ???? ?????????? ?? ??????????" + appointment.getAppointmentRegistration().getDoctor().getUser().getLastName() + " " + appointment.getAppointmentRegistration().getDoctor().getUser().getFirstName() + appointment.getFiles());

            emailSenderService.sendEmail(mailMessage);
        }
        catch (MailException e)
        {
            return new ResponseEntity<String>("???????????? ???? ?????????? ??????????????, ???? ???????????????????????????? ?????????????????? ???? ?????????? " +
                    "???????????????? ???? ??????????????", HttpStatus.OK);
        }

        return new ResponseEntity<String>("", HttpStatus.CREATED);
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
                _appointment.getFiles().addAll(appointment.getFiles());
//                _appointment.setFiles(appointment.getFiles());


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

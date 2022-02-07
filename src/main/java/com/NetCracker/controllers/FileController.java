package com.NetCracker.controllers;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.patient.File;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.exceptions.FileNotFoundException;
import com.NetCracker.repositories.patient.FileRepository;
import com.NetCracker.repositories.patient.PatientRepository;
import com.NetCracker.services.AppointmentServiceImpl;
import com.NetCracker.services.AuthenticationService;
import com.NetCracker.services.files.FileService;
import com.NetCracker.services.PatientService;
import com.NetCracker.services.files.FileViewService;
import com.NetCracker.services.files.SickListFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {

    @Autowired
    FileRepository repository;

    @Autowired
    SickListFactory sockListFactory;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PatientService patientService;

    @Autowired
    AppointmentServiceImpl appointmentService;

    @Autowired
    FileService fileService;

    @Autowired
    FileViewService fileViewService;

    @GetMapping("api/files")
    public ResponseEntity<List<File>> getAllFiles(@RequestParam(required = false) Long id) {
        try {
            List<File> files = new ArrayList<>();

            if (id == null)
                files.addAll(repository.findAllByOrderByIdAsc());
            else
                files.add(repository.findById(id).orElseThrow(() -> new FileNotFoundException(id)));

            if (files.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("api/files/id/{id}")
    public ResponseEntity<File> getFilesByPatientId(@PathVariable("id") Long id) {
        Optional<File> fileData = repository.findById(id);

        return fileData.map(file ->
                new ResponseEntity<>(file, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("api/files/{id}")
    public ResponseEntity<File> createFile(@RequestParam("files") List<MultipartFile> files, @PathVariable("id") Long id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            try {
                File _file = new File();
                for (MultipartFile file : files) {
                    _file = repository
                            .save(new File(patient.get(), file.getBytes()));
                }
                return new ResponseEntity<>(_file, HttpStatus.CREATED);
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-sick-list")
    @PreAuthorize("hasRole(ROLE_DOCTOR)")
    public ResponseEntity<String> createSickList(String fileName, Long appointmentId, LocalDate recoveryDate, Authentication authentication)
    {
        Doctor requestingDoctor = null;
        Appointment requestedAppointment = null;
        try
        {
            requestingDoctor = AuthenticationService.getAuthenticatedDoctor(authentication);
        }
        catch (ClassCastException ex)
        {
            return new ResponseEntity<String>("Срвер не смог получить информацию о Вас", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DataAccessException ex)
        {
            return new ResponseEntity<String>("Срвер не смог получить информацию из базы данных", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (requestingDoctor == null)
        {
            return new ResponseEntity<String>("В базе данных не содержится информация о пользователе, делавшем запрос", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        requestedAppointment = appointmentService.getById(appointmentId);

        if (requestedAppointment == null)
        {
            return new ResponseEntity<String>("Встреча не найдена", HttpStatus.BAD_REQUEST);
        }

        if (!requestedAppointment.getAppointmentRegistration().getDoctor().getId().equals(requestingDoctor.getId()))
        {
            return new ResponseEntity<String>("Создавать больничный может только врач, который проводит прием.", HttpStatus.UNAUTHORIZED);
        }

        XWPFDocument sickList = SickListFactory.createDocument(requestedAppointment, recoveryDate);

        ByteArrayOutputStream fileByteStream = new ByteArrayOutputStream();

        try
        {
            sickList.write(fileByteStream);
        }
        catch (IOException ex)
        {
            return new ResponseEntity<String>("Ошибка формирования документа", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        byte[] sickListBytes = fileByteStream.toByteArray();

        fileService.save(fileName, requestedAppointment.getAppointmentRegistration().getPatient(), sickListBytes);

        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @GetMapping("/files/get-by-id")
    @PreAuthorize("hasRole(ROLE_PATIENT)")
    public ResponseEntity<String> getFile(Long fileId, Authentication authentication)
    {
        Patient requestingPatient = AuthenticationService.getAuthenticatedPatient(authentication);

        if (requestingPatient == null)
        {
            return new ResponseEntity<String>("Не найден пользователь с Вашим id", HttpStatus.BAD_REQUEST);
        }

        File requestedFile;
        try
        {
            requestedFile = fileService.findById(fileId);
        }
        catch (DataAccessException ex)
        {
            return new ResponseEntity<String>("Невозможно получить файл: хранилище недоступно", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (requestedFile == null)
        {
            return new ResponseEntity<String>("Запрашиваемый файл не существует", HttpStatus.NOT_FOUND);
        }

        if (!requestedFile.getPatient().getId().equals(requestingPatient.getId()))
        {
            return new ResponseEntity<String>("У Вас нет доступа к этому файлу", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<String>(fileViewService.toJson(requestedFile), HttpStatus.OK);
    }

    @PutMapping("api/files/{file_id}/{patient_id}")
    public ResponseEntity<File> updateFile(@PathVariable("file_id") Long file_id,
                                           @PathVariable("patient_id") Long patient_id,
                                           @RequestBody MultipartFile file) throws IOException {
        Optional<File> fileData = repository.findById(file_id);
        Optional<Patient> patient = patientRepository.findById(patient_id);
        if (fileData.isPresent() && patient.isPresent()) {
            File _file = fileData.get();
            _file.setFile_data(file.getBytes());
            _file.setPatient(patient.get());
            return new ResponseEntity<>(repository.save(_file), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("api/files/{id}")
    public ResponseEntity<HttpStatus> deleteFile(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

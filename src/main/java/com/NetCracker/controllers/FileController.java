package com.NetCracker.controllers;

import com.NetCracker.entities.patient.File;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.exceptions.FileNotFoundException;
import com.NetCracker.repositories.patient.FileRepository;
import com.NetCracker.repositories.patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class FileController {

    @Autowired
    FileRepository repository;

    @Autowired
    PatientRepository patientRepository;

    @GetMapping("/files")
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

    @GetMapping("/files/id/{id}")
    public ResponseEntity<File> getFilesByPatientId(@PathVariable("id") Long id) {
        Optional<File> fileData = repository.findById(id);

        return fileData.map(file ->
                new ResponseEntity<>(file, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/files/{id}")
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

    @PutMapping("/files/{file_id}/{patient_id}")
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

    @DeleteMapping("/files/{id}")
    public ResponseEntity<HttpStatus> deleteFile(@PathVariable("id") Long id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

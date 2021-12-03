package com.NetCracker.Services;

import com.NetCracker.Entities.Patient;
import com.NetCracker.Repositories.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatientService {
    @Autowired
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient savePatient(Patient patient){
        log.info("IN UserService save {}", patient);
        return patientRepository.save(patient);
    }
}

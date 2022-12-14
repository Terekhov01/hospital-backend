package com.NetCracker.services;

import com.NetCracker.entities.patient.Patient;
import com.NetCracker.payload.Response.PatientPersinalAccountDTO;
import com.NetCracker.repositories.patient.PatientRepository;
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

    public Patient findById(Long id){

        log.info("IN UserServive getById {}",id);
        return patientRepository.findById(id).get();
    }

    public Patient findByRelatedUserId(Long id)
    {
        log.info("IN UserService findByRelatedUserId {}", id);
        return patientRepository.findByRelatedUserId(id).orElse(null);
    }

    public Long countAll()
    {
        return patientRepository.count();
    }
}

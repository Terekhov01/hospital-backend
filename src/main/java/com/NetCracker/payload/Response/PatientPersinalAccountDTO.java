package com.NetCracker.payload.Response;

import com.NetCracker.entities.patient.Patient;
import lombok.Getter;

@Getter
public class PatientPersinalAccountDTO extends UserPersonalAccountDTO
{
    Long patientId;
    String passport;
    String polys;

    public PatientPersinalAccountDTO(Patient patient)
    {
        super(patient.getUser());
        patientId = patient.getId();
        passport = patient.getPassport();
        polys = patient.getPolys();
    }
}

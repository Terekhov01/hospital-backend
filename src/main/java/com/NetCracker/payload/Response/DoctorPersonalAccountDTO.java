package com.NetCracker.payload.Response;

import com.NetCracker.entities.doctor.Doctor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
public class DoctorPersonalAccountDTO extends UserPersonalAccountDTO
{
    Long doctorId;
    String education;
    LocalDate dateOfEmployment;
    Integer room;

    public DoctorPersonalAccountDTO(Doctor doctor)
    {
        super(doctor.getUser());
        doctorId = doctor.getId();
        education = doctor.getEducation();
        dateOfEmployment = Instant.ofEpochMilli(doctor.getDateOfEmployment().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        room = doctor.getRoom().getNum();
    }
}

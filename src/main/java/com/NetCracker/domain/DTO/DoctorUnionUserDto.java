package com.NetCracker.domain.DTO;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class DoctorUnionUserDto {


    private  User user;
    private Long id;
    private String firstname;
    private String lastname;

    private Date dateOfEmployment;
    private String education;
    private Room room;
    private Set<Specialist> specialist;

    public DoctorUnionUserDto(Doctor doctor, User user){

        this.user =user;

        this.id = doctor.getId();
        this.firstname =user.getFirstName();
        this.lastname = user.getLastName();
        this.dateOfEmployment =doctor.getDateOfEmployment();
        this.education =doctor.getEducation();
        this.room =doctor.getRoom();
        this.specialist =doctor.getSpecialist();
       }

}

package com.NetCracker.domain.DTO;

import com.NetCracker.entities.doctor.Specialist;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    String firstname;
    String lastname;
    //-----
    String education;
    Integer room;
    List<Specialist> specialist;
}

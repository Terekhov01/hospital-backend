package com.NetCracker.Domain.DTO;

import com.NetCracker.Entities.Doctor.Specialist;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    String education;
    Integer room;
    List<Specialist> specialist;
}

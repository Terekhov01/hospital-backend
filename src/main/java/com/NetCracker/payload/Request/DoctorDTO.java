package com.NetCracker.payload.Request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
public class DoctorDTO extends UserDTO
{
    // https://stackoverflow.com/a/40647805/12287688
    @Pattern(regexp = "^(?!^0+$)[a-zA-Z0-9]{3,20}$")
    private String passport;

    @NotBlank
    private Integer roomNumber;

    @NotBlank
    @Size(max = 255)
    String education;

    Set<String> specializations;

    public DoctorDTO(String firstName, String lastName, String middleName, String userName, String email,
                     String password, String phone, String passport, Integer roomNumber, String education,
                     Set<String> specializations)
    {
        super(firstName, lastName, middleName, phone, userName, email, password,
                new HashSet<String>(){{ add("ROLE_DOCTOR"); }});
        this.passport = passport;
        this.roomNumber = roomNumber;
        this.education = education;
        this.specializations = specializations;
    }
}

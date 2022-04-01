package com.NetCracker.payload.Request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashSet;

@Getter
public class PatientDTO extends UserDTO
{

    @NotBlank
    // https://stackoverflow.com/a/40647805/12287688
    @Pattern(regexp = "^(?!^0+$)[a-zA-Z0-9]{3,20}$")
    private String passport;

    @NotBlank
    @Pattern(regexp = "^\\d{16}$")
    private String polys;

    public PatientDTO(String firstName, String lastName, String middleName, String userName, String email,
                      String password, String phone, String passport, String polys)
    {
        super(firstName, lastName, middleName, phone, userName, email, password,
                new HashSet<String>(){{ add("ROLE_PATIENT"); }});
        this.passport = passport;
        this.polys = polys;
    }
}

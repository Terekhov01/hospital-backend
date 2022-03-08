package com.NetCracker.payload.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;

@Getter
public class PatientSignupRequest extends UserSignupRequest
{

    @NotBlank
    // https://stackoverflow.com/a/40647805/12287688
    @Pattern(regexp = "^(?!^0+$)[a-zA-Z0-9]{3,20}$")
    private String passport;

    @NotBlank
    @Pattern(regexp = "^\\d{16}$")
    private String polys;

    public PatientSignupRequest(String firstName, String lastName, String middleName, String userName, String email,
                                String password, String phone, String passport, String polys)
    {
        super(firstName, lastName, middleName, phone, userName, email, password,
                new HashSet<String>(){{ add("ROLE_PATIENT"); }});
        this.passport = passport;
        this.polys = polys;
    }
}

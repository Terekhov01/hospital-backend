package com.NetCracker.payload.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.*;
import java.util.Set;

@Getter
@AllArgsConstructor
public class UserSignupRequest
{
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;

    // https://stackoverflow.com/a/18626090/12287688 - regex phone number validation, improved a little
    @Pattern(regexp = "\\(?\\+[0-9]{1,3}|8\\)? ?-?[0-9]{1,3} ?-?[0-9]{3,5} ?-?[0-9]{2,}( ?-?[0-9]{3})? ?(\\w{1,10}\\s?\\d{1,6})?")
    private String phone;

    @NotBlank
    @Size(min = 3, max = 20)
    private String userName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Set<String> roles;
}

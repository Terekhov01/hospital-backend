package com.NetCracker.payload.Response;

import com.NetCracker.entities.user.User;
import lombok.Getter;

@Getter
public class UserPersonalAccountDTO
{
    Long id;
    String userName;
    String firstName;
    String lastName;
    String middleName;
    String email;
    String phone;

    public UserPersonalAccountDTO(User user)
    {
        id = user.getId();
        userName = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        middleName = user.getPatronymic();
        email = user.getEmail();
        phone = user.getPhone();
    }
}

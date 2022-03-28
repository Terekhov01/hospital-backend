package com.NetCracker.payload.Response;

import com.NetCracker.entities.user.User;
import lombok.Getter;

@Getter
public class UserPersonalAccountDTO
{
    Long userId;
    String userName;
    String firstName;
    String lastName;
    String middleName;
    String email;
    String phoneNumber;

    public UserPersonalAccountDTO(User user)
    {
        userId = user.getId();
        userName = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        middleName = user.getPatronymic();
        email = user.getEmail();
        phoneNumber = user.getPhone();
    }
}

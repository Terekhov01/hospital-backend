package com.NetCracker.controllerAdvice;

import com.NetCracker.exceptions.AppointmentRegistrationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AppointmentRegistrationNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(AppointmentRegistrationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeRegistrationNotFoundHandler(AppointmentRegistrationNotFoundException ex) {
        return ex.getMessage();
    }
}

package com.NetCracker.controllerAdvice;

import com.NetCracker.exceptions.DoctorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DoctorNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(DoctorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String doctorNotFoundHandler(DoctorNotFoundException ex) {
        return ex.getMessage();
    }
}

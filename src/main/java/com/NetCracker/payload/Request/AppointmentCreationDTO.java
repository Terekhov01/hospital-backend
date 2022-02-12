package com.NetCracker.payload.Request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AppointmentCreationDTO
{
    private Long appointmentRegistrationId;

    private String description = "";

    private String recipe = "";

    private String treatPlan = "";

    private String rehabPlan = "";

    private String docStatement = "";

    private boolean sickListNeeded;

    private LocalDate recoveryDate;
}

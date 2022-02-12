package com.NetCracker.payload.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class FileRequest
{
    Long appointmentId;
    LocalDate recoveryDate;
}

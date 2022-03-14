package com.NetCracker.domain.projection;

import java.time.LocalDateTime;
import java.util.Date;

public interface DoctorStatisticProjection {

    LocalDateTime getDate();
    Long getCount();
}

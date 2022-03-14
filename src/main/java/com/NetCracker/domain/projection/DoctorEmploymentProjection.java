package com.NetCracker.domain.projection;

import java.time.LocalDateTime;

public interface DoctorEmploymentProjection {

    LocalDateTime getMonth();
    Long getSum();
}

package com.NetCracker.domain.projection;

import java.math.BigDecimal;

public interface DoctorRatingProjection {

    Integer getId();
    String getFirstName();
    String getLastName();
    BigDecimal getRating();
}

package com.NetCracker.domain.projection;

import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.doctor.Specialist;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface DoctorNamesProjection {

    Long getId();

    String getFirstname();

    String getLastname();

    List<DoctorRatingProjection> getRatings();

    Date getDateOfEmployment();

    String getEducation();

    RoomProjection getRoom();

    Set<Specialist> specialist();

}

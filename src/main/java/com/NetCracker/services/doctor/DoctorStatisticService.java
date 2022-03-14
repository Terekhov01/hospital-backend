package com.NetCracker.services.doctor;

import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.schedule.DoctorSchedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



public interface DoctorStatisticService {

//todo
    @Query(nativeQuery = true, value = "SELECT count() from doctor_schedule")
    DoctorSchedule findStatistic(long doctor_id);

}

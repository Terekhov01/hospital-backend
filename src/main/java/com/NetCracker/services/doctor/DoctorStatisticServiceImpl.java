package com.NetCracker.services.doctor;

import com.NetCracker.entities.doctor.Doctor;
//import com.NetCracker.entities.doctor.UserStub;
import com.NetCracker.entities.schedule.DoctorSchedule;
import com.NetCracker.repositories.schedule.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorStatisticServiceImpl implements DoctorStatisticService{

@Autowired
private DoctorScheduleRepository doctorScheduleRepository;
    //todo
@Override
    public DoctorSchedule findStatistic(long doctor_id) {

//        return doctorScheduleRepository.findById(doctor_id).get();
    return null;
    }

}

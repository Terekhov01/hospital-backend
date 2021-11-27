package com.NetCracker.Repositories;

import com.NetCracker.Entities.AppointmentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRegistrationRepo extends JpaRepository<AppointmentRegistration, Long> {
    //        @Query("select a from AppointmentRegistration a where a.doctor.lastName = 'ФроловВВ' order by a.id asc")
    @Query("select a from AppointmentRegistration a order by a.id asc")
    public List<AppointmentRegistration> findAllByOrderByIdAsc();

    @Query("select a from AppointmentRegistration a where a.id = :id")
    Optional<AppointmentRegistration> findById(@Param("id") Long id);

    @Query("select a from AppointmentRegistration a, Doctor d, Patient p where a.doctor.id = d.id and a.patient.id = p.id and p.lastName = :pat and d.lastName = :doc and a.start = (select min(aa.start) from AppointmentRegistration aa, Doctor dd, Patient pp where aa.doctor.id = dd.id and aa.patient.id = pp.id and pp.lastName = :pat and dd.lastName = :doc)")
    Optional<AppointmentRegistration> findByDoctorAndPatient(@Param("doc") String doc, @Param("pat") String pat);

//    @Query("select a from AppointmentRegistration a, Doctor d, Patient p where a.doctor.id = d.id and a.patient.id = p.id and p.lastName = :pat and d.lastName = :doc and a.start = :start and a.start = (select min(aa.start) from AppointmentRegistration aa)")
//    Optional<AppointmentRegistration> findByDoctorAndPatientAndStart(@Param("doc") String doc, @Param("pat") String pat, @Param("start") LocalDateTime start);
}



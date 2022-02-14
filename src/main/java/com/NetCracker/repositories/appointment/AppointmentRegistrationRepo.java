package com.NetCracker.repositories.appointment;

import com.NetCracker.entities.appointment.AppointmentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRegistrationRepo extends JpaRepository<AppointmentRegistration, Long> {
//            @Query("select a from AppointmentRegistration a where a.doctor.lastName = 'Фролов В.В.' order by a.id asc")
    @Query("select a from AppointmentRegistration a order by a.id asc")
    List<AppointmentRegistration> findAllByOrderByIdAsc();

    @Query("select a from AppointmentRegistration a where a.id = :id")
    Optional<AppointmentRegistration> findById(@Param("id") Long id);

    @Query("select a from AppointmentRegistration a where a.patient.user.id = :id")
    List<AppointmentRegistration> findAllByPatient(@Param("id") Long id);

    @Query("select a from AppointmentRegistration a where a.doctor.user.id = :id")
    List<AppointmentRegistration> findAllByDoctor(@Param("id") Long id);

    @Query("select a from AppointmentRegistration a, Doctor d, Patient p where a.doctor.id = d.id and a.patient.id = p.id and p.user.lastName = :pat and d.user.lastName = :doc and a.start = (select min(aa.start) from AppointmentRegistration aa, Doctor dd, Patient pp where aa.doctor.id = dd.id and aa.patient.id = pp.id and pp.user.lastName = :pat and dd.user.lastName = :doc)")
    Optional<AppointmentRegistration> findByDoctorAndPatient(@Param("doc") String doc, @Param("pat") String pat);

//    @Query("select a from AppointmentRegistration a, Doctor d, Patient p where a.doctor.id = d.id and a.patient.id = p.id and p.lastName = :pat and d.lastName = :doc and a.start = :start and a.start = (select min(aa.start) from AppointmentRegistration aa)")
//    Optional<AppointmentRegistration> findByDoctorAndPatientAndStart(@Param("doc") String doc, @Param("pat") String pat, @Param("start") LocalDateTime start);
}



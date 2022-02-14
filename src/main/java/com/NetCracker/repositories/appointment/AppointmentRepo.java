package com.NetCracker.repositories.appointment;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.appointment.AppointmentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
//        @Query("select a from Appointment a where a.appointmentRegistration.doctor.lastName = 'Фролов В.В.' order by a.id asc")
    @Query("select a from Appointment a order by a.id asc")
    List<Appointment> findAllByOrderByIdAsc();

    @Query("select a from Appointment a where a.appointmentRegistration.doctor.user.id = :id")
    List<Appointment> findAllByDoctor(@Param("id") Long id);

    @Query("select a from Appointment  a where a.appointmentRegistration.patient.user.id = :id")
    List<Appointment> findAllByPatient(@Param("id") Long id);

    @Query("select a from Appointment a where a.id = :id")
    Optional<Appointment> findById(@Param("id") Long id);

    @Query("select a from Appointment a where a.appointmentRegistration.start = (select max(aa.start) from AppointmentRegistration aa where aa.patient.id = :id) and a.appointmentRegistration.patient.id = :id")
    Optional<Appointment> findLastAppointment(@Param("id") Long id);

    Optional<Appointment> findByAppointmentRegistration(AppointmentRegistration appointmentRegistration);

}


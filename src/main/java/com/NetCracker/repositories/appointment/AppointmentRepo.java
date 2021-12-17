package com.NetCracker.repositories.appointment;

import com.NetCracker.entities.appointment.Appointment;
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

    @Query("select a from Appointment a where a.id = :id")
    Optional<Appointment> findById(@Param("id") Long id);

}


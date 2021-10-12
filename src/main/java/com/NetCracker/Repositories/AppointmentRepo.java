package com.NetCracker.Repositories;

import com.NetCracker.Entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Integer> {
    @Query("select a from Appointment a order by a.start asc")
    public List<Appointment> findAllByOrderByStartAsc();
}

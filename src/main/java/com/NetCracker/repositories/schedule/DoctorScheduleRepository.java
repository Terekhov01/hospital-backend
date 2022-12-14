package com.NetCracker.repositories.schedule;

import com.NetCracker.entities.schedule.DoctorSchedule;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;


@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long>
{
    @Query(value = "SELECT * FROM doctor_schedule WHERE doctor_id IN :doctorIds", nativeQuery = true)
    Set<DoctorSchedule> findByRelatedDoctor(@Param("doctorIds") Collection<Long> doctorIds) throws DataAccessException;

    @Query(value = "SELECT * FROM doctor_schedule WHERE doctor_id = :doctorId", nativeQuery = true)
    DoctorSchedule findByRelatedDoctor(@Param("doctorId") Long doctorId) throws DataAccessException;

    @Query("select ds from DoctorSchedule ds where ds.relatedDoctor.user.id = :docId")
    Optional<DoctorSchedule> findByDoctorId(@Param("docId") Long id);

    /*@Query(value = "FROM doctor_schedule JOIN doctor.name IN :")
    DoctorSchedule findByRelatedDoctorName(Collection<String> doctorNames);

    @Query(value = "SELECT * FROM ")
    DoctorSchedule findByRelatedDoctorName(String doctorName);*/
}

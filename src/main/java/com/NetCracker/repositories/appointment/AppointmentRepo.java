package com.NetCracker.repositories.appointment;

import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.appointment.AppointmentRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Query("select a from Appointment a where a.appointmentRegistration.doctor.user.id = :id order by a.appointmentRegistration.start desc")
    Page<Appointment> findAllByDoctorPaged(Pageable pr, Long id);

    @Query("select a from Appointment a where a.appointmentRegistration.doctor.user.id = :id " +
            "and ( a.recipe like %:keyWord% " +
            "or a.docStatement like %:keyWord% " +
            "or a.description like %:keyWord% " +
            "or a.rehabPlan like %:keyWord% " +
            "or a.treatPlan like %:keyWord% " +
            "or a.appointmentRegistration.patient.user.lastName like %:keyWord% " +
            "or a.appointmentRegistration.patient.user.firstName like %:keyWord% " +
            "or a.appointmentRegistration.patient.user.patronymic like %:keyWord% " +
            "or (select concat(aa.appointmentRegistration.patient.user.lastName, ' ', aa.appointmentRegistration.patient.user.firstName, ' ', aa.appointmentRegistration.patient.user.patronymic) from Appointment aa where aa.id = a.id) like %:keyWord%)" +
//            "and (:keyWord is null or cast(a.appointmentRegistration.start as string) like %:keyWord%)" +
            " order by a.appointmentRegistration.start desc")
    Page<Appointment> findAllByDoctorAndKeyWordPaged(@Param("keyWord") String keyWord, Pageable pr, Long id);

}


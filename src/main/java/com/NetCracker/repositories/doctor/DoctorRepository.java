package com.NetCracker.repositories.doctor;

import com.NetCracker.domain.projection.DoctorNamesProjection;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("select a from Doctor a order by a.id asc")
    List<Doctor> findAllByOrderByIdAsc();

    /*@Query("select a from Doctor a where a.id = :id")
    Optional<Doctor> findById(@Param("id") Long id);*/

    @Query("select a from Doctor a where a.user.lastName = :doc")
    Optional<Doctor> findDoctorByLastName(@Param("doc") String doc);

    void delete(Doctor user);

    List<Doctor> findAll();

    Optional<Doctor> findById(Long id);

    Optional<Doctor> findByUser_id(Long id);

    Doctor save(Doctor user);

    //findByIdInOrderBySpecializationAscNameAsc
    @Query(nativeQuery = false, value = "FROM Doctor doctor WHERE id IN :id ORDER BY doctor.user.lastName ASC, doctor.user.firstName ASC")
    List<Doctor> findByIdInOrderByLastNameAscFirstNameAsc(@Param("id") Collection<Long> doctorIds);

    @Query("FROM Doctor doctor INNER JOIN doctor.specialist specialist INNER JOIN doctor.user user WHERE specialist.specialization = :specializationName ORDER BY user.lastName")
    List<Doctor> findBySpecializationName(@Param("specializationName") String specializationName);

    //Collection<DoctorShortInformation> findByIdIn(Collection<Long> id);
    @Query("select a from Doctor a")
    List<Doctor> fixedFindAll();

}

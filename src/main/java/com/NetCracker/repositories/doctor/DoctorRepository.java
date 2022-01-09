package com.NetCracker.repositories.doctor;

import com.NetCracker.domain.projection.DoctorNamesProjection;
import com.NetCracker.entities.doctor.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    //@Query("select a from Service a order by a.id asc")

    @Query(nativeQuery = false, value = "FROM Doctor doctor WHERE doctor.id IN :id ORDER BY doctor.user.lastName ASC, doctor.user.firstName ASC")
    List<Doctor> findByIdInOrderByLastNameAscFirstNameAsc(@Param("id") Collection<Long> doctorIds);

//    Collection<DoctorShortInformation> findByIdIn(Collection<Long> id);
@Query("select a from Doctor a")
List<Doctor> fixedFindAll();

}

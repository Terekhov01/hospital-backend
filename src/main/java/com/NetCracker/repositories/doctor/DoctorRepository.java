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
//    @Query(nativeQuery = true, value = "SELECT u.id,u.firstname,u.lastname,AVG(z.rate)
//    AS rating FROM users u
//    INNER JOIN users_rating z  on u.id = z.users_id GROUP BY u.id, u.firstname, u.lastname")
//    List<UserStub> findAll();

//    @Query("SELECT User.id, User.firstName, User.lastName, Doctor.dateOfEmployment, Doctor.education, Doctor.room, Doctor.specialist, Doctor.ratings" +
//            " from User INNER JOIN Doctor ON User.id = Doctor.id")
//    @Query("Select u,d From User u JOIN Doctor d ON User.id = Doctor.id")

//    @Query(value = "SELECT u.id                 AS id,\n" +
//            "       u.first_name         AS firstname,\n" +
//            "       u.last_name          AS lastname,\n" +
//            "       d.date_of_employment AS dateOfEmployment,\n" +
//            "       d.education          AS education,\n" +
//            "       d.room_id            AS room,\n" +
//            "       s.specialist_id      AS specialist,\n" +
//            "       dr.doctor_id         AS ratings\n" +
//            "from doctor d\n" +
//            "         INNER JOIN users u ON u.id = d.id\n" +
//            "         LEFT JOIN doctors_specialist s ON d.id = s.doctor_id\n" +
//            "         LEFT JOIN doctors_rating dr on d.id = dr.doctor_id"
//            , nativeQuery = true
//    )

//    @Query(value = "SELECT u.id                 AS id,\n" +
//            "       u.first_name         AS firstname,\n" +
//            "       u.last_name          AS lastname,\n" +
//            "       d.date_of_employment AS dateOfEmployment,\n" +
//            "       d.education          AS education,\n" +
//            "       (SELECT * from Room r WHERE r.id = d.room_id)               AS room,\n" +
//
//            "\n" +
//            "from doctor d\n" +
//            "         INNER JOIN users u ON u.id = d.id\n"
//
//
//
//            , nativeQuery = true
//    )

//    @Query("SELECT u.id AS id, u.firstName AS firstname, u.lastName AS lastname, d.dateOfEmployment " +
//            "AS dateOfEmployment, d.education AS education,d.room AS room," +
//            "s.specialization AS specialist, " +
//            "dr.doctor AS ratings " +
//           " from Doctor d INNER JOIN FETCH User u " +
//            "LEFT JOIN FETCH Specialist s " +
//            "LEFT JOIN FETCH DoctorRating dr "
//    )

//    @Query("SELECT u.id AS id, u.firstName AS firstname, u.lastName AS lastname, d.dateOfEmployment " +
//            "AS dateOfEmployment, d.education AS education," +
////            "(SELECT r FROM Room r WHERE d.room.id = r.id  ) AS room  " +
//            "(SELECT s FROM Specialist s WHERE s.doctors = d.specialist ) AS specialist " +
////            "dr.doctor AS ratings " +
//           " from Doctor d INNER JOIN FETCH User u ON d.id = u.id "
////            "LEFT JOIN FETCH Specialist s " +
////            "LEFT JOIN FETCH DoctorRating dr " +
////            "LEFT JOIN FETCH Room r ON d.room.id = r.id "
//    )


//
//        @Query("SELECT d.id AS id,d.dateOfEmployment AS dateOfEmployment, d.education AS education," +
//                "(SELECT r  FROM Room r WHERE d.room.id = r.id )" +
//                "FROM Doctor d "
//
//    )
//
//    List<DoctorNamesProjection> names();

    List<Doctor> findAll();

    Optional<Doctor> findById(Long id);

    Optional<Doctor> findByUser_id(Long id);

    Doctor save(Doctor user);

    //findByIdInOrderBySpecializationAscNameAsc
    //@Query("select a from Service a order by a.id asc")

    @Query(nativeQuery = false, value = "FROM Doctor doctor WHERE doctor.id IN :id ORDER BY doctor.user.lastName ASC, doctor.user.firstName ASC")
    List<Doctor> findByIdInOrderByLastNameAscFirstNameAsc(@Param("id") Collection<Long> doctorIds);

//    Collection<DoctorShortInformation> findByIdIn(Collection<Long> id);

}

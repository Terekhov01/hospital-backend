package com.NetCracker.repositories.doctor;

import com.NetCracker.domain.projection.DoctorRatingProjection;
import com.NetCracker.entities.doctor.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRatingRepository extends JpaRepository<DoctorRating,Integer> {

//   @Query(nativeQuery = true, value = "SELECT u.id AS id,u.firstname AS firstName,u.lastname AS lastName,AVG(z.rate) AS rating " +
//           "FROM doctor u LEFT JOIN doctors_rating z  on u.id = z.doctor_id " +
//           "GROUP BY u.id, u.firstname, u.lastname")

   @Query(nativeQuery = true, value = "SELECT\n" +
           "    u.id AS id,\n" +
           "    u.first_name AS firstName,\n" +
           "    u.last_name AS lastName,\n" +
           "    AVG(dr.rating) AS rating\n" +
           "FROM\n" +
           "    users u\n" +
           "        RIGHT JOIN\n" +
           "    doctor d on u.id = d.user_id\n" +
           "\n" +
           "LEFT JOIN doctors_rating dr on d.id = dr.doctor_id\n" +
           "GROUP BY\n" +
           "    u.id,\n" +
           "    u.first_name,\n" +
           "    u.last_name")
    List<DoctorRatingProjection> getRating();



//   @Query(nativeQuery = true, value = "SELECT u.id AS id,u.first_name AS firstName,u.last_name AS lastName,AVG(z.rate) AS rating " +
//           "FROM users u INNER JOIN doctors_rating z  on u.id = z.user_id " +
//           "GROUP BY u.id, u.first_name, u.last_name")
//    List<DoctorRatingProjection> getRating();

}

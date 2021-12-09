package com.NetCracker.Repositories.Doctor;

import com.NetCracker.Domain.DTO.DoctorRatingProjection;
import com.NetCracker.Entities.Doctor.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRatingRepository extends JpaRepository<DoctorRating,Integer> {

   @Query(nativeQuery = true, value = "SELECT u.id AS id,u.firstname AS firstName,u.lastname AS lastName,AVG(z.rate) AS rating " +
           "FROM doctor u LEFT JOIN doctors_rating z  on u.id = z.doctor_id " +
           "GROUP BY u.id, u.firstname, u.lastname")
    List<DoctorRatingProjection> getRating();

//   @Query(nativeQuery = true, value = "SELECT u.id AS id,u.first_name AS firstName,u.last_name AS lastName,AVG(z.rate) AS rating " +
//           "FROM users u INNER JOIN doctors_rating z  on u.id = z.user_id " +
//           "GROUP BY u.id, u.first_name, u.last_name")
//    List<DoctorRatingProjection> getRating();

}

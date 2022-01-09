package com.NetCracker.services;

import com.NetCracker.entities.doctor.UserStub;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AllUserService {

    List<UserStub> findByRoleLike(String role);
//    List<AllUsers> findAll();
UserStub findById(int id);

//    @Query(nativeQuery = true, value = "SELECT u.id,u.firstname,u.lastname,AVG(z.rate) AS rating FROM users u INNER JOIN users_rating z  on u.id = z.users_id GROUP BY u.id, u.firstname, u.lastname")
    @Query(nativeQuery = true, value = "SELECT u.id,u.first_name,u.last_name,AVG(z.rating) AS rating FROM users u INNER JOIN doctors_rating z  on u.id = z.doctor_id GROUP BY u.id, u.first_name, u.last_name")
    List<UserStub> findAll();
}
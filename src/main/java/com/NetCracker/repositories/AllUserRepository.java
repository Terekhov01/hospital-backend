package com.NetCracker.repositories;

import com.NetCracker.entities.doctor.UserStub;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface AllUserRepository extends Repository<UserStub,Integer> {

//    @Query(value = "SELECT AVG(r.rating ) FROM AllUsers r WHERE   ")
//    AllUsers findByRoleLike(@Param());
@Query(value = "SELECT a FROM UserStub a WHERE a.role like '1'")
    List<UserStub> findByRoleLike(String role);

//List<AllUsers> findAll();
    Optional<UserStub> findById(int id);

    UserStub save(UserStub user);

}
